package com.example.reactorexamples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Locale;
import java.util.stream.Stream;

@SpringBootApplication
public class ReactorExamplesApplication {

	public static void main(String[] args) {
		Mono.just("Hi")
				.map(e -> e.toUpperCase(Locale.ROOT))
				.map(e -> e + " hi")
				.subscribe(System.out::println);
		Stream<String> stringStream = Stream.of("a", "b", "c");
		Flux.fromStream(stringStream)
				.subscribe(System.out::println);

		Mono.just("Craig")
						.map(String::toUpperCase)
						.map(cn -> "Hello, " + cn + "!")
								.subscribe(System.out::println);

		Flux<Integer> range = Flux.range(3, 1);

		Flux.interval(Duration.ofSeconds(1))
				.take(5);

		Flux<String> fruitFlux = Flux.just("Apple", "Orange", "Grape", "Banana", "Strawberry");
		fruitFlux.subscribe(System.out::print);

		Flux<String> firstFlux = Flux.just("1", "3", "5");
		Flux<String> secondFlux = Flux.just("2", "4");
		Flux<String> mergedFlux = firstFlux.mergeWith(secondFlux);



		SpringApplication.run(ReactorExamplesApplication.class, args);
	}

}
