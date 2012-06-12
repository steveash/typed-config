import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import com.github.steveash.typedconfig.annotation.Config;
import com.github.steveash.typedconfig.annotation.ConfigProxy;
import com.github.steveash.typedconfig.ConfigProxyFactory;
import com.github.steveash.typedconfig.Option;


public class ConfigProxyDemo {
	@ConfigProxy
	static interface CarConfiguration {
		@Config(value="doors", defaultValue = "4")
		int getDoors();

		@Config(value="[@name]")
		String getName();

		@Config(value="air-conditioning", options = Option.CHECK_KEY_EXISTS)
		boolean hasAirConditioning();
	}

	public static void main(final String[] args) throws ConfigurationException {
		CarConfiguration configuration = ConfigProxyFactory.getDefault().make(CarConfiguration.class, new XMLConfiguration("car.xml"));

		System.out.println("Building a '" + configuration.getName() +
				"' with " + configuration.getDoors() + " doors");

		if (configuration.hasAirConditioning()) {
			System.out.println("This car has airconditioning");
		}
	}
}
