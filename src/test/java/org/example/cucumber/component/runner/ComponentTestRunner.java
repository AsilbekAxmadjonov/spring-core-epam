package org.example.cucumber.component.runner;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/component/authentication")
@SelectClasspathResource("features/component/training")
@ConfigurationParameter(
        key = GLUE_PROPERTY_NAME,
        value = "org.example.cucumber.component.config,org.example.cucumber.component.authentication,org.example.cucumber.component.training"
)
@ConfigurationParameter(
        key = PLUGIN_PROPERTY_NAME,
        value = "pretty, summary, html:target/cucumber-reports/component-tests.html"
)
public class ComponentTestRunner {
}