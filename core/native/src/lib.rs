use jni::JNIEnv;
use jni::objects::{JClass, JString};
use jni::sys::jstring;

use std::ptr;

pub mod libusb;
pub mod librtlsdr;

#[unsafe(no_mangle)]
pub unsafe extern "system" fn Java_dev_zt64_sdrmapper_NativeSdr_test<'local>(
    env: JNIEnv<'local>,
    _class: JClass<'local>,
    _input: JString<'local>,
) -> jstring {
    let mut ctx: *mut libusb::libusb_context = ptr::null_mut();
    let result = unsafe { libusb::libusb_init(&mut ctx) };

    if result != 0 {
        panic!("Failed to initialize libusb: {}", result);
    } else {
        println!("libusb initialized successfully");
    }

    return env.new_string("Hello from Rust!").expect("Couldn't create java string!").into_raw();
}
