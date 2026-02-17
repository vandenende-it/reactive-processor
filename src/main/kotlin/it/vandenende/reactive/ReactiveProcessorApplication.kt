package it.vandenende.reactive

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import reactor.core.publisher.Flux
import java.util.function.Consumer

// Een simpel data-model (POJO/DTO)
data class TempReading(val sensor_id: Int, val temp: Double)

@SpringBootApplication
class ReactiveProcessorApplication {

	@Bean
	fun processTemperature(): Consumer<Flux<TempReading>> {
		return Consumer { input ->
			input
				// 1. Groepeer de data in vensters van 5 seconden
				.window(java.time.Duration.ofSeconds(5))
				// 2. Schakel over naar de inhoud van het venster
				.flatMap { window ->
					window.collectList().map { list ->
						val avg = if (list.isNotEmpty()) list.map { it.temp }.average() else 0.0
						"📊 [STATISTIEK] Gemiddelde over 5 sec: ${"%.2f".format(avg)}°C (Aantal metingen: ${list.size})"
					}
				}
				.subscribe { println(it) }
		}
	}
}

fun main(args: Array<String>) {
	runApplication<ReactiveProcessorApplication>(*args)
}