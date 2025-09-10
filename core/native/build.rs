use std::{env, fs, path::PathBuf, process::Command};
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

    build_libusb(&target_abi);
    build_rtlsdrblog(&target_abi);
    generate_bindings();

    if let Ok(jni_libs) = env::var("CARGO_NDK_OUTPUT_PATH") {
        let jni_abi_dir = PathBuf::from(&jni_libs).join(target_abi);
        let out_dir = PathBuf::from(env::var("OUT_DIR").unwrap());

        if fs::create_dir_all(&jni_abi_dir).is_ok() {
            let lib_dir = out_dir.join("lib");
            let src_libusb = lib_dir.join("libusb1.0.so");
            let dst_libusb = jni_abi_dir.join("libusb1.0.so");
            let _ = fs::copy(&src_libusb, &dst_libusb);

            let src_rtlsdr = lib_dir.join("librtlsdr.so");
            let dst_rtlsdr = jni_abi_dir.join("librtlsdr.so");
            let _ = fs::copy(&src_rtlsdr, &dst_rtlsdr);
        }
    }
}

fn generate_bindings() {
    let out_dir = PathBuf::from(env::var("OUT_DIR").unwrap());
    let include_dir = out_dir.join("include");

    let libusb_header_path = include_dir.join("libusb/libusb.h");
    bindgen::Builder::default()
        .header(libusb_header_path.to_str().unwrap())
        .parse_callbacks(Box::new(bindgen::CargoCallbacks::new()))
        .generate()
        .expect("Unable to generate libusb bindings")
        .write_to_file(out_dir.join("libusb_bindings.rs"))
        .expect("Couldn't write libusb bindings");

//     let rtlsdr_header_path = include_dir.join("rtl-sdr_export.h");
//     bindgen::Builder::default()
//         .header(rtlsdr_header_path.to_str().unwrap())
//         .parse_callbacks(Box::new(bindgen::CargoCallbacks::new()))
//         .generate()
//         .expect("Unable to generate rtlsdr bindings")
//         .write_to_file(out_dir.join("librtlsdr_bindings.rs"))
//         .expect("Couldn't write rtlsdr bindings");
}

fn build_libusb(target_abi: &str) {
    let ndk_path = env::var("ANDROID_NDK_HOME").expect("ANDROID_NDK_HOME environment variable must be set");
    let libusb_dir = env::current_dir().unwrap().join("libusb");

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

    fs::copy(&libusb_path, out_dir.join("libusb1.0.so")).expect("Failed to copy libusb1.0.so to OUT_DIR");

    println!("cargo:rustc-link-search=native={}", out_dir.display());
    println!("cargo:rustc-link-lib=dylib=usb1.0");
}

fn build_rtlsdrblog(target_abi: &str) {
    let ndk_path = env::var("ANDROID_NDK_HOME").expect("ANDROID_NDK_HOME environment variable must be set");
    let out_dir = PathBuf::from(env::var("OUT_DIR").unwrap());

    let include_dir = out_dir.join("include");
    let lib_dir = out_dir.join("lib");

    let _dst = cmake::Config::new("rtl-sdr")
        .define("CMAKE_TOOLCHAIN_FILE", format!("{}/build/cmake/android.toolchain.cmake", ndk_path))
        .define("ANDROID_ABI", target_abi)
        .define("ANDROID_ARM_NEON", "ON")
        .define("THREADS_PTHREAD_ARG", "-pthread")
        .define("BUILD_UTILITIES", "OFF")
        .define("CMAKE_INSTALL_PREFIX", out_dir.to_str().unwrap())
        .define("CMAKE_INSTALL_INCLUDEDIR", "include")
        .define("CMAKE_INSTALL_FULL_INCLUDEDIR", include_dir.to_str().unwrap())
        .cflag(format!("-I{}", include_dir.to_str().unwrap()))
        .define("CMAKE_LIBRARY_PATH", lib_dir.to_str().unwrap())
        .define("CMAKE_EXE_LINKER_FLAGS", format!("-L{}", lib_dir.to_str().unwrap()))
        .define("CMAKE_SHARED_LINKER_FLAGS", format!("-L{}", lib_dir.to_str().unwrap()))
        .build();

    fs::copy(
        &lib_dir.join("librtlsdr.so"),
        &out_dir.join("librtlsdr.so")
    ).expect("Failed to copy librtlsdr.so to OUT_DIR");

    println!("cargo:rustc-link-search=native={}", lib_dir.display());
    println!("cargo:rustc-link-lib=dylib=rtlsdr");
}