package mikeshafter.mikestcaddons;

import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.Plugin;

import java.util.Stack;


public class SignActionBarrel extends SignAction {
  
  @Override
  public boolean match(SignActionEvent event) {
    return event.isType("barrel", "special");
  }
  
  @Override
  public void execute(SignActionEvent event) {
    Plugin plugin = MikesTCAddons.getPlugin(MikesTCAddons.class);
    // When a [train] sign is placed, activate when powered by redstone when the train
    // goes over the sign, or when redstone is activated.
    // When a [cart] sign is placed, activate when powered by redstone when each cart
    // goes over the sign, or when redstone is activated.
    if ((event.isTrainSign() && event.isAction(SignActionType.GROUP_ENTER, SignActionType.REDSTONE_ON) && event.isPowered() && event.hasGroup()) || (event.isCartSign() && event.isAction(SignActionType.MEMBER_ENTER, SignActionType.REDSTONE_ON) && event.isPowered() && event.hasMember())) {
  
      // Barrel
      BlockState state = event.getAttachedBlock().getState();
      if (state instanceof Container container) {
        for (ItemStack itemStack : container.getInventory().getContents()) {
          if (itemStack != null && itemStack.hasItemMeta() && itemStack.getItemMeta() instanceof BookMeta bookMeta) {
            for (String page : bookMeta.getPages()) {
          
              // parse pages
              // remove ending \n's
          
              //stack to handle brackets
              Stack<String> stack = new Stack<>();
          
            }
          }
      
        }
      }
  
    }
    
    
  }
  
  @Override
  public boolean build(SignChangeActionEvent event) {
    return SignBuildOptions.create()
        .setName("barrel")
        .setDescription("do a lot of stuff")
        .handle(event.getPlayer());
  }
}
