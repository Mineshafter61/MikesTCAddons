package mikeshafter.mikestcaddons.rh;
import com.bergerkiller.bukkit.common.config.ConfigurationNode;
import com.bergerkiller.bukkit.tc.Util;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.properties.api.ICartProperty;
import com.bergerkiller.bukkit.tc.properties.api.ITrainProperty;
import java.util.Optional;

public class RHProperties {

public static final ICartProperty<Double> WEIGHT = new ICartProperty<>() {
	@Override
	public Double getDefault () {
		return 1d;
	}

	@Override
	public Optional<Double> readFromConfig (ConfigurationNode config) {
		return Util.getConfigOptional(config, "weight", Double.class);
	}

	@Override
	public void writeToConfig (ConfigurationNode config, Optional<Double> value) {
		Util.setConfigOptional(config, "weight", value);
	}
};
public static final ICartProperty<Float> MOTOR = new ICartProperty<>() {
	@Override
	public Float getDefault () {
		return 0f;
	}

	@Override
	public Optional<Float> readFromConfig (ConfigurationNode config) {
		return Util.getConfigOptional(config, "motor", Float.class);
	}

	@Override
	public void writeToConfig (ConfigurationNode config, Optional<Float> value) {
		Util.setConfigOptional(config, "motor", value);
	}
};
private static final ITrainProperty<String> COMPANY = new ITrainProperty<>() {
	@Override
	public String getDefault () {
		return "";
	}

	@Override
	public Optional<String> readFromConfig (ConfigurationNode config) {
		return Util.getConfigOptional(config, "company", String.class);
	}

	@Override
	public void writeToConfig (ConfigurationNode config, Optional<String> value) {
		Util.setConfigOptional(config, "company", value);
	}
};

public static double getWeight (final MinecartMember<?> m) {
	return WEIGHT.get(m.getProperties());
}

public static double getStaticFriction (final MinecartMember<?> m) {
	return WEIGHT.get(m.getProperties()) * 0.8;
}

public static double getDynamicFriction (final MinecartMember<?> m) {
	return WEIGHT.get(m.getProperties()) * 0.62;
}

public static void setWeight (final MinecartMember<?> m, double i) {
	WEIGHT.set(m.getProperties(), i);
}

public static float getMotor (final MinecartMember<?> m) {
	return MOTOR.get(m.getProperties());
}

public static void setMotor (final MinecartMember<?> m, float i) {
	MOTOR.set(m.getProperties(), i);
}

public static String getCompany (final MinecartMember<?> m) {
	return COMPANY.get(m.getProperties());
}

public static void setCompany (final MinecartMember<?> m, String s) {
	COMPANY.set(m.getProperties(), s);
}

}
