package mikeshafter.mikestcaddons;

import com.bergerkiller.bukkit.tc.events.SignActionEvent;
import com.bergerkiller.bukkit.tc.events.SignChangeActionEvent;
import com.bergerkiller.bukkit.tc.signactions.SignAction;
import com.bergerkiller.bukkit.tc.signactions.SignActionType;
import com.bergerkiller.bukkit.tc.utils.SignBuildOptions;
import org.bukkit.Material;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.Plugin;


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
      Block block = event.getAttachedBlock();
      // TODO: Debug
      plugin.getLogger().info(block.getType().toString());
      if (block instanceof Barrel barrel) {
        ItemStack itemStack = barrel.getInventory().getItem(0);
        if (itemStack != null && (itemStack.getType() == Material.WRITABLE_BOOK || itemStack.getType() == Material.WRITTEN_BOOK)) {
          BookMeta bookMeta = (BookMeta) itemStack.getItemMeta();
          
          String[] pages = new String[bookMeta.getPageCount()];
          
          for (int i = 1; i < pages.length+1; ++i) {
            pages[i] = bookMeta.getPage(i);
            
            // TODO: Debug
            plugin.getLogger().info(bookMeta.getPage(i));
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
