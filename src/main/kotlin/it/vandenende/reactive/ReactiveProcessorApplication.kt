package it.vandenende.reactive

import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import reactor.core.publisher.Flux
import java.time.Duration

// 1. Zorg dat deze class matcht met wat de Python generator stuurt
data class SensorData(
	val temperature: Double = 0.0,
	val timestamp: Long = 0
)

// 2. De data class die naar het dashboard (BFF) gaat
data class DashboardMetric(
	val temp: Double = 0.0,
	val count: Long = 0
)

@SpringBootApplication
class ReactiveProcessorApplication {
	private val logger = LoggerFactory.getLogger(javaClass)

	@Bean
	fun processTemperature(): (Flux<SensorData>) -> Flux<DashboardMetric> {
		return { input ->
			input
				.window(Duration.ofSeconds(5))
				.flatMap { window ->
					window.collectList().map { list ->
						val avg = if (list.isNotEmpty()) list.map { it.temperature }.average() else 0.0

						if (avg > 25.0) {
							logger.warn("🔥 Kritieke hitte gedetecteerd: $avg")
						}

						logger.info("📊 Aggregatie voltooid: $avg over ${list.size} metingen")
						DashboardMetric(temp = avg, count = list.size.toLong())
					}
				}
		}
	}
}

fun main(args: Array<String>) {
	runApplication<ReactiveProcessorApplication>(*args)
}