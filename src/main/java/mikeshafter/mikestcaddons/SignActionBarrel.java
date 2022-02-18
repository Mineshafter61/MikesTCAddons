package mikeshafter.mikestcaddons;

import bsh.EvalError;
import bsh.Interpreter;
import com.bergerkiller.bukkit.tc.TrainCarts;
import com.bergerkiller.bukkit.tc.controller.MinecartGroup;
import com.bergerkiller.bukkit.tc.controller.MinecartMember;
import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.properties.TrainProperties;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class SignActionBarrel extends SignAction {
  
  @Override
  public boolean match(SignActionEvent event) {
    return event.isType("barrel", "special");
  }
  
  @Override
  public void execute(SignActionEvent event) {
    // When a [train] sign is placed, activate when powered by redstone when the train
    // goes over the sign, or when redstone is activated.
    // When a [cart] sign is placed, activate when powered by redstone when each cart
    // goes over the sign, or when redstone is activated.
    if ((event.isTrainSign() && event.isAction(SignActionType.GROUP_ENTER, SignActionType.REDSTONE_ON) && event.isPowered() && event.hasGroup())) {
    
      // Barrel
      BlockState state = event.getAttachedBlock().getState();
      if (state instanceof Container container) {
  
        // StringBuilder to build code
        StringBuilder builder = new StringBuilder();
  
        for (ItemStack itemStack : container.getInventory().getContents()) {
          if (itemStack != null && itemStack.hasItemMeta() && itemStack.getItemMeta() instanceof BookMeta bookMeta) {
      
            for (Component page : bookMeta.pages()) {
              TextComponent pageText = (TextComponent) page;
              // parse pages and append page
              builder.append(pageText.content());
            }
          }
        }
  
        // Code to be parsed
        String code = builder.toString();
  
        // Get group and properties for later
        MinecartGroup group = event.getGroup();
        TrainProperties trainProperties = (TrainProperties) group.getProperties().clone();
  
        // Execute code using BeanShell interpreter
        Interpreter i = new Interpreter();  // Construct an interpreter
  
        try {
  
          i.eval("import org.bukkit.*;");
          i.eval("com.bergerkiller.bukkit.*");
          // Set extra variables
          i.set("tc", new BarrelMethods(group));  // Custom methods
          i.set("properties", trainProperties);  // General properties
          // Other properties
          i.set("bankingSmoothness", trainProperties.getBankingSmoothness());
          i.set("bankingStrength", trainProperties.getBankingStrength());
          i.set("canOnlyOwnersEnter", trainProperties.getCanOnlyOwnersEnter());
          i.set("collision", trainProperties.getCollision());
          i.set("collisionDamage", trainProperties.getCollisionDamage());
          i.set("config", trainProperties.getConfig());
          i.set("currentRouteDestinationIndex", trainProperties.getCurrentRouteDestinationIndex());
          i.set("destination", trainProperties.getDestination());
          i.set("destinationRoute", trainProperties.getDestinationRoute());
          i.set("displayName", trainProperties.getDisplayName());
          i.set("displayNameOrEmpty", trainProperties.getDisplayNameOrEmpty());
          i.set("friction", trainProperties.getFriction());
          i.set("gravity", trainProperties.getGravity());
          i.set("holder", trainProperties.getHolder());
          i.set("killMessage", trainProperties.getKillMessage());
          i.set("lastPathNode", trainProperties.getLastPathNode());
          i.set("location", trainProperties.getLocation());
          i.set("nextDestinationOnRoute", trainProperties.getNextDestinationOnRoute());
          i.set("ownerPermissions", trainProperties.getOwnerPermissions());
          i.set("owners", trainProperties.getOwners());
          i.set("playersEnter", trainProperties.getPlayersEnter());
          i.set("playersExit", trainProperties.getPlayersExit());
          i.set("skipOptions", trainProperties.getSkipOptions());
          i.set("spawnItemDrops", trainProperties.getSpawnItemDrops());
          i.set("speedLimit", trainProperties.getSpeedLimit());
          i.set("tags", trainProperties.getTags());
          i.set("tickets", trainProperties.getTickets());
          i.set("trainName", trainProperties.getTrainName());
          i.set("typeName", trainProperties.getTypeName());
          i.set("waitAcceleration", trainProperties.getWaitAcceleration());
          i.set("waitDeceleration", trainProperties.getWaitDeceleration());
          i.set("waitDelay", trainProperties.getWaitDelay());
          i.set("waitDistance", trainProperties.getWaitDistance());
    
          // Evaluate code
          i.eval(code);
    
        } catch (EvalError e) {
          // spit out the entire error message to everyone on the train
          for (MinecartMember<?> member : group) {
            member.getEntity().getPlayerPassengers().forEach(player -> TrainCarts.sendMessage(player, e.toString()));
          }
        }
      }
    }
  }
  
  
  @Override
  public boolean build(SignChangeActionEvent event) {
    if (event.getPlayer().hasPermission("mikestcaddons.barrel")) {
      return SignBuildOptions.create().setName("barrel").setDescription("do a lot of stuff").handle(event.getPlayer());
    } else {
      event.setCancelled(true);
      return false;
    }
  }
}
