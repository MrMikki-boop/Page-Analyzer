# Makefile
.DEFAULT_GOAL := build-run



build: # Build app
	./gradlew clean
	./gradlew installDist

report:
		./gradlew jacocoTestReport

run-dist: # Run app
		./build/install/app/bin/app

run:
	./gradlew run

test:
		./gradlew test

lint:
	./gradlew checkstyleMain checkstyleTest

last: # Last version plugin
	./gradlew useLatestVersions

build-run: build run-dist
build-test: build test report

.PHONY: build