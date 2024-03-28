package mikeshafter.mikestcaddons;

import com.bergerkiller.bukkit.common.Common;
import com.bergerkiller.bukkit.common.map.archive.MapResourcePackArchive;
import com.bergerkiller.bukkit.common.map.archive.MapResourcePackAutoArchive;
import com.bergerkiller.bukkit.common.map.archive.MapResourcePackClientArchive;
import com.bergerkiller.generated.net.minecraft.server.MinecraftServerHandle;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

public class ResourcePackHandler {

public InputStream getSound (String path) {
	String fullPath = "assets/minecraft/sounds/" + stripNS(path) + ".ogg";

	MinecraftServerHandle mcs = MinecraftServerHandle.instance();
	String resourcePackPath = mcs.getResourcePack();
	String resourcePackHash = mcs.getResourcePackHash();
	MapResourcePackArchive archive = new MapResourcePackClientArchive();

	if (!(resourcePackPath == null || resourcePackPath.isEmpty() ||
		resourcePackPath.equalsIgnoreCase("vanilla") || resourcePackPath.equalsIgnoreCase("default")
	))
		archive = new MapResourcePackAutoArchive(resourcePackPath, resourcePackHash);


	try {
		InputStream stream = archive.openFileStream(fullPath);
		if (stream == null) {
			stream = archive.openFileStream(fullPath.toLowerCase(Locale.ENGLISH));
		}
		if (stream != null) {
			return stream;
		}
	}
	catch (IOException ignored) {
	}

	// Fallback
	return Common.class.getResourceAsStream("/com/bergerkiller/bukkit/common/internal/resources/sounds" + stripNS(path) + ".ogg");
}

public double getSoundLength (String path) {
	InputStream soundInputStream = getSound(path);
	double duration;
	try {
		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundInputStream);
		AudioFormat format = audioInputStream.getFormat();
		duration = (double) audioInputStream.getFrameLength() / format.getFrameRate();
	}
	catch (UnsupportedAudioFileException | IOException e) {
		throw new RuntimeException(e);
	}
	return duration;
}

private static String stripNS(String path) {
	int namespaceIndex = path.indexOf(':');
	return (namespaceIndex == -1) ? path : path.substring(namespaceIndex+1);
}

}
