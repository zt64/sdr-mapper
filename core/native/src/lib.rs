use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::jstring;

use std::ptr;

// Include separate bindings for each library
pub mod libusb {
    #![allow(non_upper_case_globals)]
    #![allow(non_camel_case_types)]
    #![allow(non_snake_case)]
    #![allow(warnings)]
    include!(concat!(env!("OUT_DIR"), "/libusb_bindings.rs"));
}

pub mod librtlsdr {
    #![allow(non_upper_case_globals)]
    #![allow(non_camel_case_types)]
    #![allow(non_snake_case)]
    #![allow(warnings)]
    include!(concat!(env!("OUT_DIR"), "/librtlsdr_bindings.rs"));
}

pub mod libairspy {
    #![allow(non_upper_case_globals)]
    #![allow(non_camel_case_types)]
    #![allow(non_snake_case)]
    #![allow(warnings)]
    include!(concat!(env!("OUT_DIR"), "/libairspy_bindings.rs"));
}

pub mod libairspyhf {
    #![allow(non_upper_case_globals)]
    #![allow(non_camel_case_types)]
    #![allow(non_snake_case)]
    #![allow(warnings)]
    include!(concat!(env!("OUT_DIR"), "/libairspyhf_bindings.rs"));
}

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_dev_zt64_sdrmapper_NativeSdr_test<'local>(
    env: JNIEnv<'local>,
    _class: JClass<'local>,
    _input: JString<'local>,
) -> jstring {
    let mut ctx: *mut libusb::libusb_context = ptr::null_mut();
    let result = unsafe { libusb::libusb_init(&mut ctx) };

    if result != 0 {
        // panic!("Failed to initialize libusb: {}", result);
    } else {
        println!("libusb initialized successfully");
    }

    return env.new_string("Hello from Rust!").expect("Couldn't create java string!").into_raw();
}
