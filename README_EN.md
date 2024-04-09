# Business-Day-Calculator

## Description
The Business Day Calculator is a Java utility specifically configured for Colombian holidays, designed to determine the next business day given a specific date and time. This tool factors in weekends, public holidays, special non-working days specific to Colombia, and business hours to accurately predict the next suitable business day for operations.

## Features
- Customized for Colombian public holidays and special dates.
- Determines the next business day considering weekends and non-working hours.
- Provides a flexible rule set for calculating holidays and special observances.

## Requirements
- Java JDK 8 or higher
- Maven for dependency management and project building

## Installation
1. Clone this repository: `git clone [repo-link]`.
2. Navigate to the project directory and run `mvn clean install` to build the project and install the dependencies.

## Usage
Create an instance of the `BusinessDayCalculator` and invoke the `getNextBusinessDay` method with the desired `LocalDateTime` parameter to obtain the next business day as a `LocalDateTime` object.
