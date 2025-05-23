package com.app.server.main_app_service

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MainAppServiceApplication

fun main(args: Array<String>) {
	runApplication<MainAppServiceApplication>(*args)
}
