plugins{
    kotlin("jvm")
    kotlin("kapt")
}

dependencies{
    implementation(project(":annotations"))
    implementation(kotlin("stdlib"))

    implementation("com.squareup:kotlinpoet:0.7.0")

    implementation("com.google.auto.service:auto-service:1.0-rc4")
    kapt("com.google.auto.service:auto-service:1.0-rc4")

}