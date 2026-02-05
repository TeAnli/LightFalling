package top.teanli.lightfalling.accessor;

public interface IMinecraftClient {
    void setItemUseCooldown(int itemUseCooldown);
    int getItemUseCooldown();
    boolean invokeDoAttack();
    void invokeDoItemUse();
}
