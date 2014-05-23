/*
 * Copyright 2014 University of California, Berkeley.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Author: Michael Pradel

package evaluation;

import java.io.File;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class OctaneExperimentRunner {

	final String firefoxBinary = "/home/m/javascript/mozilla-central/obj-x86_64-unknown-linux-gnu/dist/firefox/firefox";
	final String jalangiFFxpi = "/home/m/research/projects/Jalangi-Berkeley/browserExtensions/jalangiFF/jalangiff.xpi";

	final String firefoxLogFile = "/tmp/firefox.out";
	final String javascriptLogFile = "/tmp/firefox_javascript.out";

	String baseUrl = "http://127.0.0.1:8000/tests/octane2/";
	String emptyPageUrl = "http://127.0.0.1:8000/tests/inconsistentType/empty.html";
	WebDriver driver;
	int maxWaitTime = 5*60;

	public static void main(String[] args) throws Exception {
		assert(args.length == 1);
		new OctaneExperimentRunner().run(args[0]);
	}

	private void run(String benchmark) throws Exception {
		DesiredCapabilities desiredCapabilities = DesiredCapabilities.firefox();
		LoggingPreferences loggingPreferences = new LoggingPreferences();
		loggingPreferences.enable(LogType.BROWSER, Level.ALL);
		desiredCapabilities.setCapability(CapabilityType.LOGGING_PREFS,
				loggingPreferences);
		FirefoxBinary binary = new FirefoxBinary(new File(firefoxBinary));
		FirefoxProfile profile = new FirefoxProfile();
		System.setProperty("webdriver.firefox.logfile", firefoxLogFile);
		profile.setPreference("webdriver.log.file", javascriptLogFile);
		profile.setPreference("dom.max_script_run_time", 120);
		profile.setPreference("extensions.sdk.console.logLevel", "info");
		profile.addExtension(new File(jalangiFFxpi));
		driver = new FirefoxDriver(binary, profile, desiredCapabilities);
		driver.manage().timeouts().implicitlyWait(120, TimeUnit.SECONDS);

		testBenchmark(benchmark);

		// trigger beforeunload event after last benchmark
//		driver.get("about:blank");

		System.out.println("Almost done");
//		driver.quit();

		System.out.println("Done :-)");
	}

	
	public void testBenchmark(String benchmark) throws Exception {
		driver.get(baseUrl+"index_"+benchmark+".html?auto=1");
		(new WebDriverWait(driver, maxWaitTime)).until(ExpectedConditions.textToBePresentInElementLocated(By.id("main-banner"), "Score"));
		driver.get(emptyPageUrl);
		
	}
	
}