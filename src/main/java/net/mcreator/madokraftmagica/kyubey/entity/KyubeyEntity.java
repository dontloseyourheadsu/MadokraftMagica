package net.mcreator.madokraftmagica.kyubey.entity;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkHooks;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import net.mcreator.madokraftmagica.kyubey.menu.KyubeyContractMenuProvider;

import javax.annotation.Nonnull;

public class KyubeyEntity extends PathfinderMob implements IAnimatable {
    private AnimationFactory factory;
    private Player interactingPlayer = null;
    private int interactionCooldown = 0;
    private boolean isInteracting = false;
    private boolean wasMoving = false; // Track previous movement state

    public KyubeyEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.factory = new AnimationFactory(this); // Keeping deprecated for now, still works
    }

    public static AttributeSupplier.Builder createAttributes() {
        return PathfinderMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 16.0D);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }

    @Override
    public InteractionResult mobInteract(@Nonnull Player player, @Nonnull InteractionHand hand) {
        if (!this.level.isClientSide && hand == InteractionHand.MAIN_HAND) {
            if (interactionCooldown <= 0 && !isInteracting) {
                // Start interaction state
                this.startInteraction(player);

                // Open the contract menu
                if (player instanceof ServerPlayer serverPlayer) {
                    NetworkHooks.openScreen(serverPlayer, new KyubeyContractMenuProvider(this),
                            buf -> buf.writeInt(this.getId()));
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    public void startInteraction(Player player) {
        this.isInteracting = true;
        this.interactingPlayer = player;
        this.interactionCooldown = 200; // 10 seconds cooldown

        // Stop all movement
        this.getNavigation().stop();
        this.goalSelector.disableControlFlag(net.minecraft.world.entity.ai.goal.Goal.Flag.MOVE);
        this.goalSelector.disableControlFlag(net.minecraft.world.entity.ai.goal.Goal.Flag.LOOK);

        // Clear current path and target
        this.getNavigation().recomputePath();
    }

    public void endInteraction() {
        this.isInteracting = false;
        this.interactingPlayer = null;

        // Re-enable movement
        this.goalSelector.enableControlFlag(net.minecraft.world.entity.ai.goal.Goal.Flag.MOVE);
        this.goalSelector.enableControlFlag(net.minecraft.world.entity.ai.goal.Goal.Flag.LOOK);
    }

    @Override
    public void tick() {
        super.tick();

        if (interactionCooldown > 0) {
            interactionCooldown--;
        }

        if (isInteracting && interactingPlayer != null) {
            // Check if player is still valid and nearby
            if (interactingPlayer.isAlive() && this.distanceToSqr(interactingPlayer) < 64) {
                // Make Kyubey look directly at the player's face
                double dx = interactingPlayer.getX() - this.getX();
                double dy = (interactingPlayer.getEyeY() - this.getEyeY());
                double dz = interactingPlayer.getZ() - this.getZ();

                // Calculate yaw (horizontal rotation)
                double horizontalDistance = Math.sqrt(dx * dx + dz * dz);
                float yaw = (float) (Math.atan2(dz, dx) * (180D / Math.PI)) - 90.0F;

                // Calculate pitch (vertical rotation)
                float pitch = (float) -(Math.atan2(dy, horizontalDistance) * (180D / Math.PI));

                // Smoothly rotate towards the player
                this.setYRot(yaw);
                this.setXRot(pitch);
                this.yHeadRot = yaw;
                this.yBodyRot = yaw;

                // Keep navigation stopped
                this.getNavigation().stop();
            } else {
                // Player too far or invalid, end interaction
                this.endInteraction();
            }
        }
    }

    @Override
    public void registerControllers(AnimationData animationData) {
        AnimationController<KyubeyEntity> controller = new AnimationController<>(this, "movement", 0,
                this::animationPredicate);
        animationData.addAnimationController(controller);
    }

    private <E extends IAnimatable> PlayState animationPredicate(AnimationEvent<E> event) {
        // Multiple movement detection methods for better accuracy
        double horizontalSpeed = this.getDeltaMovement().horizontalDistanceSqr();
        boolean hasVelocity = horizontalSpeed > 0.005D;
        boolean hasNavigationPath = this.getNavigation().getPath() != null && !this.getNavigation().isDone();
        boolean isActuallyMoving = hasVelocity || hasNavigationPath;

        // Override movement detection during interaction
        if (isInteracting) {
            event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.kyubey.idle"));
            this.wasMoving = false;
            return PlayState.CONTINUE;
        }

        // Check if movement state changed to trigger animation update
        if (isActuallyMoving != wasMoving) {
            this.wasMoving = isActuallyMoving;

            if (isActuallyMoving) {
                // Start walking animation
                event.getController().markNeedsReload();
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.kyubey.walk"));
            } else {
                // Start idle animation
                event.getController().markNeedsReload();
                event.getController().setAnimation(new AnimationBuilder().addAnimation("animation.kyubey.idle"));
            }
        }

        return PlayState.CONTINUE;
    }

    @Override
    public AnimationFactory getFactory() {
        return this.factory;
    }
}
