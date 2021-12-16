plugins {
    id("solidblocks.kotlin-library-conventions")
}

dependencies {
    implementation(project(":solidblocks-cloud-config"))
    implementation(project(":solidblocks-api"))
    implementation(project(":solidblocks-base"))
    implementation("org.freemarker:freemarker:2.3.31")

    api("me.tomsdevsn:hetznercloud-api:2.13.0")
    implementation("io.pelle.hetzner:hetznerdns-java:0.1.0")
}
