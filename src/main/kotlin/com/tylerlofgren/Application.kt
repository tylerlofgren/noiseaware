package com.tylerlofgren

import io.micronaut.runtime.Micronaut.*
fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("com.tylerlofgren")
		.start()
}

