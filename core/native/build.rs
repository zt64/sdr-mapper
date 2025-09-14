use std::{env, fs, path::{Path, PathBuf, MAIN_SEPARATOR}, process::Command};
use cmake;

fn main() {
    let target = env::var("TARGET").unwrap_or_else(|_| String::from("unknown"));
    let target_abi = match target.as_str() {
        "armv7-linux-androideabi" => "armeabi-v7a",
        "aarch64-linux-android" => "arm64-v8a",
        "i686-linux-android" => "x86",
        "x86_64-linux-android" => "x86_64",
        _ => "arm64-v8a",
    };
    let out_dir = PathBuf::from(env::var("OUT_DIR").unwrap());

    build_libusb(&target_abi);
    build_rtlsdrblog(&target_abi);
    build_airspyhf(&target_abi);
    build_airspyone_host(&target_abi);
    // build_fftw(&target_abi);
    // build_hackrf(&target_abi);
    generate_bindings(&out_dir);

    if let Ok(jni_libs) = env::var("CARGO_NDK_OUTPUT_PATH") {
        let jni_abi_dir = PathBuf::from(&jni_libs).join(target_abi);

        if fs::create_dir_all(&jni_abi_dir).is_ok() {
            let lib_dir = out_dir.join("lib");
            let libs = vec![
                "libairspyhf.so",
                "libairspy.so",
                "librtlsdr.so",
                "libusb1.0.so",
                // "libfftw3f.so",
                // "libhackrf.so",
            ];

            for lib in libs {
                let src = lib_dir.join(lib);
                let dst = jni_abi_dir.join(lib);
                let _ = fs::copy(&src, &dst);
            }
        }
    }
}

fn binary_output_directory() -> PathBuf {
    let out_dir = env::var("OUT_DIR").unwrap();
    let out_dir = out_dir.as_str();

    let target = env::var("TARGET").unwrap();
    let profile = env::var("PROFILE").unwrap();
    let host = env::var("HOST").unwrap();
    if target == host {
        let separator = format!("target{}{}", MAIN_SEPARATOR, profile);
        if let Some(idx) = out_dir.find(&separator) {
            return PathBuf::from(String::from(&out_dir[..(idx + separator.len())]));
        }
    }

    let separator = format!(
        "target{}{}{}{}",
        MAIN_SEPARATOR, target, MAIN_SEPARATOR, profile
    );
    let idx = out_dir.find(&separator).unwrap();
    PathBuf::from(String::from(&out_dir[..(idx + separator.len())]))
}

fn generate_binding(header_path: &Path, lib_name: &str) {
    let out_dir = PathBuf::from(env::var("OUT_DIR").unwrap());
    let include_dir = out_dir.join("include");

    bindgen::Builder::default()
        .header(header_path.to_str().unwrap())
        .clang_arg(format!("-I{}", include_dir.display()))
        .parse_callbacks(Box::new(bindgen::CargoCallbacks::new()))
        .generate()
        .unwrap_or_else(|_| panic!("Unable to generate {} bindings", lib_name))
        .write_to_file(out_dir.join(lib_name.to_owned() + "_bindings.rs"))
        .unwrap_or_else(|_| panic!("Couldn't write {} bindings", lib_name));
}

fn generate_bindings(out_dir: &PathBuf) {
    let include_dir = out_dir.join("include");

    let bindings = [
        (include_dir.join("libusb/libusb.h"), "libusb"),
        (include_dir.join("rtl-sdr.h"), "librtlsdr"),
        (include_dir.join("libairspy/airspy.h"), "libairspy"),
        (include_dir.join("libairspyhf/airspyhf.h"), "libairspyhf"),
    ];

    for (header_path, lib_name) in bindings {
        generate_binding(&header_path, lib_name);
    }
}

fn build_libusb(target_abi: &str) {
    let ndk_path = env::var("ANDROID_NDK_HOME").expect("ANDROID_NDK_HOME environment variable must be set");
    let libusb_dir = env::current_dir().unwrap().join("external/libusb");

    Command::new(format!("{}/ndk-build", ndk_path))
        .current_dir(libusb_dir.join("android/jni"))
        .status()
        .expect("Failed to execute ndk-build")
        .success()
        .then(|| ())
        .expect("Failed to build libusb with ndk-build");

    let out_dir = PathBuf::from(env::var("OUT_DIR").unwrap());
    let include_dir = out_dir.join("include");
    let lib_dir = out_dir.join("lib");
    let libusb_path = libusb_dir.join(format!("android/libs/{}/libusb1.0.so", target_abi));

    fs::create_dir_all(include_dir.join("libusb")).expect("Failed to create include directory");
    fs::create_dir_all(&lib_dir).expect("Failed to create lib directory");

    fs::copy(&libusb_dir.join("libusb/libusb.h"), include_dir.join("libusb/libusb.h"))
        .expect("Failed to copy libusb.h");
    fs::copy(&libusb_dir.join("libusb/libusbi.h"), include_dir.join("libusb/libusbi.h"))
        .expect("Failed to copy libusbi.h");

    fs::copy(&libusb_path, &lib_dir.join("libusb1.0.so")).expect("Failed to copy libusb1.0.so");
    fs::copy(&libusb_path, &lib_dir.join("libusb-1.0.so")).expect("Failed to copy libusb-1.0.so");

    copy_to_out("libusb1.0.so");

    println!("cargo:rustc-link-search=native={}", out_dir.display());
    println!("cargo:rustc-link-lib=dylib=usb1.0");
}

fn cmake_build(package: &str, options: &[(&str, &str)], target_abi: &str) {
    let ndk_path = PathBuf::from(env::var("ANDROID_NDK_HOME").expect("ANDROID_NDK_HOME environment variable must be set"));
    let out_dir = PathBuf::from(env::var("OUT_DIR").unwrap());
    let include_dir = out_dir.join("include");
    let lib_dir = out_dir.join("lib");

    let mut config = cmake::Config::new(package);

    config
        .define("CMAKE_TOOLCHAIN_FILE", ndk_path.join("build/cmake/android.toolchain.cmake"))
        .define("ANDROID_ABI", target_abi)
        .define("ANDROID_ARM_NEON", "ON")
        .define("THREADS_PTHREAD_ARG", "-pthread")
        .define("BUILD_UTILITIES", "OFF")
        .define("CMAKE_copy_to_out_PREFIX", out_dir.to_str().unwrap())
        .define("CMAKE_copy_to_out_INCLUDEDIR", "include")
        .define("CMAKE_copy_to_out_FULL_INCLUDEDIR", include_dir.to_str().unwrap())
        .define("CMAKE_LIBRARY_PATH", lib_dir.to_str().unwrap())
        .define("CMAKE_EXE_LINKER_FLAGS", format!("-L{}", lib_dir.to_str().unwrap()))
        .define("CMAKE_SHARED_LINKER_FLAGS", format!("-L{}", lib_dir.to_str().unwrap()))
        .define("CMAKE_WARN_DEPRECATED", "OFF")
        .cflag(format!("-I{}", include_dir.to_str().unwrap()));

    for (key, value) in options {
        config.define(key, value);
    }

    config.build();
}

fn copy_to_out(path: &str) {
    let lib_dir = PathBuf::from(env::var("OUT_DIR").unwrap()).join("lib");

    fs::copy(
        &lib_dir.join(path),
        binary_output_directory().join(path),
    ).expect(format!("Failed to copy {path} to OUT_DIR").as_str());
}

fn build_rtlsdrblog(target_abi: &str) {
    let lib_dir = PathBuf::from(env::var("OUT_DIR").unwrap()).join("lib");

    cmake_build("external/rtl-sdr", &[], target_abi);
    copy_to_out("librtlsdr.so");

    println!("cargo:rustc-link-search=native={}", lib_dir.display());
    println!("cargo:rustc-link-lib=dylib=rtlsdr");
}

fn build_airspyhf(target_abi: &str) {
    let out_dir = PathBuf::from(env::var("OUT_DIR").unwrap());
    let lib_dir = out_dir.join("lib");

    let libusb_include_path = out_dir.join("include").join("libusb");
    let libusb_lib_path = lib_dir.join("libusb1.0.so");

    let options = [
        ("LIBUSB_INCLUDE_DIR", libusb_include_path.to_str().unwrap()),
        ("LIBUSB_LIBRARIES", libusb_lib_path.to_str().unwrap()),
        ("CMAKE_POLICY_VERSION_MINIMUM", "3.5"),
    ];

    cmake_build("external/airspyhf", &options, target_abi);
    copy_to_out("libairspyhf.so");

    println!("cargo:rustc-link-search=native={}", lib_dir.display());
    println!("cargo:rustc-link-lib=dylib=airspyhf");
}

fn build_airspyone_host(target_abi: &str) {
    let out_dir = PathBuf::from(env::var("OUT_DIR").unwrap());
    let include_dir = out_dir.join("include");
    let lib_dir = out_dir.join("lib");

    let libusb_include_path = include_dir.join("libusb");
    let libusb_lib_path = lib_dir.join("libusb1.0.so");

    let options = [
        ("LIBUSB_INCLUDE_DIR", libusb_include_path.to_str().unwrap()),
        ("LIBUSB_LIBRARIES", libusb_lib_path.to_str().unwrap()),
        ("CMAKE_POLICY_VERSION_MINIMUM", "3.5"),
    ];

    cmake_build("external/airspyone_host", &options, target_abi);

    copy_to_out("libairspy.so");
    println!("cargo:rustc-link-search=native={}", lib_dir.display());
    println!("cargo:rustc-link-lib=dylib=airspy");
}

fn build_fftw(target_abi: &str) {
    let out_dir = PathBuf::from(env::var("OUT_DIR").unwrap());
    let lib_dir = out_dir.join("lib");

    let options = [
        ("ENABLE_FLOAT", "ON"),
        ("BUILD_TESTS", "OFF"),
        ("CMAKE_POLICY_VERSION_MINIMUM", "3.5"),
    ];

    cmake_build("external/fftw3", &options, target_abi);
    copy_to_out("libfftw3f.so");

    println!("cargo:rustc-link-search=native={}", lib_dir.display());
    println!("cargo:rustc-link-lib=dylib=fftw3f");
}

fn build_hackrf(target_abi: &str) {
    let out_dir = PathBuf::from(env::var("OUT_DIR").unwrap());
    let include_dir = out_dir.join("include");
    let lib_dir = out_dir.join("lib");

    let libusb_include_path = include_dir.join("libusb");
    let libusb_lib_path = lib_dir.join("libusb1.0.so");
    let fftw_lib_path = lib_dir.join("libfftw3f.so");

    let options = [
        ("LIBUSB_INCLUDE_DIR", libusb_include_path.to_str().unwrap()),
        ("LIBUSB_LIBRARIES", libusb_lib_path.to_str().unwrap()),
        ("CMAKE_POLICY_VERSION_MINIMUM", "3.5"),
    ];

    cmake_build("external/hackrf/host", &options, target_abi);
    copy_to_out("libhackrf.so");

    println!("cargo:rustc-link-search=native={}", lib_dir.display());
    println!("cargo:rustc-link-lib=dylib=hackrf");
}