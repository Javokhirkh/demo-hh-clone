package org.example.demohhclone

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
class DemoHhCloneApplication

fun main(args: Array<String>) {
    runApplication<DemoHhCloneApplication>(*args)
}
