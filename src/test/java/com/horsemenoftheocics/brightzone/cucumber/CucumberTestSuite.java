package com.horsemenoftheocics.brightzone.cucumber;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = {"classpath:com/carleton/comp5104/cms/cucumberfeature"},
        glue = {"com.horsemenoftheocics.brightzone.cucumber"})
public class CucumberTestSuite {
}
