package com.example.reactorexamples;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class ReactorExamplesApplicationTests {

	@Test
	void contextLoads() {
		Flux<String> fruitFlux = Flux.just("Apple", "Orange", "Grape", "Banana", "Strawberry");
		StepVerifier.create(fruitFlux)
				.expectNext("Apple")
				.expectNext("Orange")
				.expectNext("Grape")
				.expectNext("Banana")
				.expectNext("Strawberry")
				.verifyComplete();
	}

	@Test
	void mergeFluxes() {
		Flux<String> firstFlux = Flux.just("Garfield", "Kojak", "Barbossa")
				.delayElements(Duration.ofMillis(500));
		Flux<String> secondFlux = Flux.just("Lasagna", "Lollipops", "Apples")
				.delaySubscription(Duration.ofMillis(250))
				.delayElements(Duration.ofMillis(500));
		Flux<String> mergedFlux = firstFlux.mergeWith(secondFlux);

		StepVerifier.create(mergedFlux)
				.expectNext("Garfield")
				.expectNext("Lasagna")
				.expectNext("Kojak")
				.expectNext("Lollipops")
				.expectNext("Barbossa")
				.expectNext("Apples")
				.verifyComplete();


	}

	@Test
	void zipFlux() {
			Flux<String> characterFlux = Flux
				.just("Garfield", "Kojak", "Barbossa");
		Flux<String> foodFlux = Flux
				.just("Lasagna", "Lollipops", "Apples");

		Flux<String> zippedFlux =
				Flux.zip(characterFlux, foodFlux, (c, f) -> c + " eats " + f);

		StepVerifier.create(zippedFlux)
				.expectNext("Garfield eats Lasagna")
				.expectNext("Kojak eats Lollipops")
				.expectNext("Barbossa eats Apples")
				.verifyComplete();
	}

	@Test
	public void skipAFlux() {
		Flux<String> countFlux = Flux.just("one", "two", "skip a few", "ninety nine", "one hundred").skip(3);

		StepVerifier.create(countFlux)
				.expectNext("ninety nine", "one hundred")
				.verifyComplete();
	}

	@Test
	public void skipAFewSeconds() {
		Flux<String> countFlux = Flux.just("one", "two", "skip a few", "ninety nine", "one hundred")
				.delayElements(Duration.ofSeconds(1))
				.skip(Duration.ofSeconds(4));

		StepVerifier.create(countFlux)
				.expectNext("ninety nine", "one hundred")
				.verifyComplete();

	}

	@Test
	public void teke() {
		Flux<String> nationalParkFlux = Flux.just(
						"Yellowstone", "Yosemite", "Grand Canyon", "Zion", "Acadia")
				.take(3);
		StepVerifier.create(nationalParkFlux)
				.expectNext("Yellowstone", "Yosemite", "Grand Canyon")
				.verifyComplete();
	}

	@Test
	void takeForAWhile() {
		Flux<String> nationalParkFlux = Flux.just("Yellowstone", "Yosemite", "Grand Canyon", "Zion", "Grand Teton")
				.delayElements(Duration.ofSeconds(1))
				.take(Duration.ofMillis(3500));

		StepVerifier.create(nationalParkFlux)
				.expectNext("Yellowstone", "Yosemite", "Grand Canyon")
				.verifyComplete();

	}

	@Test
	void filter() {
		Flux<String> nationalParkFlux = Flux.just("Yellowstone", "Yosemite", "Grand Canyon", "Zion", "Grand Teton")
				.filter(np -> !np.contains(" "));

		StepVerifier.create(nationalParkFlux)
				.expectNext("Yellowstone", "Yosemite", "Zion")
				.verifyComplete();
	}

	@Test
	void distinct() {
		Flux<String> animalFlux = Flux.just("dog", "cat", "bird", "dog", "bird", "anteater")
				.distinct();

		StepVerifier.create(animalFlux)
				.expectNext("dog", "cat", "bird", "anteater")
				.verifyComplete();

	}

	@Test
	public void map() {
		Flux<Player> playerFlux = Flux
				.just("Michael Jordan", "Scottie Pippen", "Steve Kerr")
				.map(n -> {
					String[] split = n.split("\\s");
					return new Player(split[0], split[1]);
				});

		StepVerifier.create(playerFlux)
				.expectNext(new Player("Michael", "Jordan"))
				.expectNext(new Player("Scottie", "Pippen"))
				.expectNext(new Player("Steve", "Kerr"))
				.verifyComplete();
	}

		private record Player(String firstName, String lastName) {
	}

	@Test
	void flatMap() {
		Flux<Player> playerFlux = Flux
				.just("Michael Jordan", "Scottie Pippen", "Steve Kerr")
				.flatMap(n -> Mono.just(n)
					.map(p -> {
						String [] split = p.split("\\s");
						return new Player(split[0], split[1]);
					})
				.subscribeOn(Schedulers.parallel())
				);

		List<Player> playerList = Arrays.asList(
				new Player("Michael", "Jordan"),
				new Player("Scottie", "Pippen"),
				new Player("Steve", "Kerr"));

		StepVerifier.create(playerFlux)
				.expectNextMatches(playerList::contains)
				.expectNextMatches(playerList::contains)
				.expectNextMatches(playerList::contains)
				.verifyComplete();

	}

}
