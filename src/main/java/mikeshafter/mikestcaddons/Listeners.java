package mikeshafter.mikestcaddons;

//
//import com.bergerkiller.bukkit.common.utils.ParseUtil;
//import org.bukkit.block.Block;
//import org.bukkit.block.BlockFace;
//import org.bukkit.block.Sign;
//import org.bukkit.block.data.BlockData;
//import org.bukkit.event.EventHandler;
//import org.bukkit.event.block.BlockBreakEvent;
//import org.bukkit.event.block.SignChangeEvent;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Locale;
//import java.util.Objects;
//
//
public class Listeners implements org.bukkit.event.Listener {
//  @EventHandler
//  public void onSignPlace(SignChangeEvent event) {
//    if (!Objects.requireNonNull(event.line(0)).toString().equalsIgnoreCase("[door]")) {
//      return;
//    }
//    // second line gives the name of the door
//    String name = Objects.requireNonNull(event.line(1)).toString();
//
//    // third line gives the time the door is open
//    long time = ParseUtil.parseTime(Objects.requireNonNull(event.line(2)).toString()) / 50;
//
//    // fourth line gives the direction of opening
//    String directionString = Objects.requireNonNull(event.line(3)).toString().toUpperCase(Locale.ENGLISH);
//    if (Arrays.asList("UP", "DOWN", "NORTH", "SOUTH", "EAST", "WEST", "U", "D", "N", "S", "E", "W").contains(directionString)) {
//      if (directionString.length() == 1) {
//        directionString = switch (directionString) {
//          case "U" -> "UP";
//          case "D" -> "DOWN";
//          case "E" -> "EAST";
//          case "W" -> "WEST";
//          case "N" -> "NORTH";
//          case "S" -> "SOUTH";
//          default -> ""
//        };
//      }
//      BlockFace direction = BlockFace.valueOf(directionString);
//
//    }
//  }
//
//  @EventHandler
//  public void onSignBreak(BlockBreakEvent event) {
//    if ( !(event.getBlock() instanceof Sign && ((Sign) event.getBlock()).line(0).toString().equalsIgnoreCase("[door]") )) return;
//    Sign sign = (Sign) event.getBlock();
//    String name = sign.line(1).toString();
//  }
}
