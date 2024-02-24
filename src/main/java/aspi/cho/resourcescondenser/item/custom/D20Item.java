package aspi.cho.resourcescondenser.item.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class D20Item extends Item {
    public D20Item(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if(!level.isClientSide() && hand == InteractionHand.MAIN_HAND) {
            int randNum = getRandomNumber();
            if (randNum > 10){
                outputMessageToPlayer(player, "You are lucky today! Your Number is " + randNum);
                player.addEffect(new MobEffectInstance(MobEffects.LUCK, 12600, 5 ));
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600, 3 ));
                player.addEffect(new MobEffectInstance(MobEffects.GLOWING, 80, 1));
            }
            else {
                outputMessageToPlayer(player, "You are unlucky... Your Number is " + randNum);
                player.addEffect(new MobEffectInstance(MobEffects.UNLUCK, 1200, 5 ));
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 600, 2 ));
                player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 80, 1));
                player.hurt(DamageSource.playerAttack(player), 1f);
            }
            player.getCooldowns().addCooldown(this, 600);
        }

        return super.use(level, player, hand);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> components, TooltipFlag flag) {
        if(Screen.hasShiftDown()) {
            components.add(Component.translatable("tooltip.resources_condenser.d20.advanced").withStyle(ChatFormatting.AQUA));
        } else {
            components.add(Component.translatable("tooltip.resources_condenser.d20"));
            components.add(Component.translatable("tooltip.resources_condenser.more_info").withStyle(ChatFormatting.YELLOW));
        }
        super.appendHoverText(itemStack, level, components, flag);
    }

    private void outputMessageToPlayer(Player player, String message) {
        player.sendSystemMessage(Component.literal(message));
    }
    private int getRandomNumber() {
        return RandomSource.createNewThreadLocalInstance().nextInt(20) + 1;
    }
}
