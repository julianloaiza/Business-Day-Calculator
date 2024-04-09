# Calculadora de Días Hábiles

## Descripción
La Calculadora de Días Hábiles es una utilidad en Java configurada para los festivos de Colombia, diseñada para determinar el siguiente día hábil a partir de una fecha y hora específicas. Esta herramienta tiene en cuenta los fines de semana, festivos públicos, días especiales no laborables específicos de Colombia y horas hábiles para predecir con precisión el próximo día hábil adecuado para operaciones.

## Características
- Personalizada para festivos y fechas especiales de Colombia.
- Determina el próximo día hábil considerando fines de semana y horas no laborables.
- Ofrece un conjunto de reglas flexible para calcular festivos y celebraciones especiales.

## Requisitos
- Java JDK 8 o superior
- Maven para la gestión de dependencias y construcción del proyecto

## Instalación
1. Clona este repositorio: `git clone [repo-link]`.
2. Navega al directorio del proyecto y ejecuta `mvn clean install` para construir el proyecto e instalar las dependencias.

## Uso
Crea una instancia de `BusinessDayCalculator` e invoca el método `getNextBusinessDay` con el parámetro `LocalDateTime` deseado para obtener el siguiente día hábil como un objeto `LocalDateTime`.
