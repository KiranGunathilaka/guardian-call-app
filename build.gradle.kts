buildscript {
    dependencies {
        classpath(libs.google.services)
        classpath("com.google.gms:google-services:4.2.0")

    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.androidApplication) apply false
}