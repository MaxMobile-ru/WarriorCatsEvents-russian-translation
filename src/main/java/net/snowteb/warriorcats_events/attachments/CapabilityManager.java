package net.snowteb.warriorcats_events.attachments;

import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.attachment.AttachmentType;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class CapabilityManager {

    public static <T> void attachmentProvider(Player player, Supplier<AttachmentType<T>> attachment, Consumer<T> consumer) {
        T data = player.getData(attachment);
        consumer.accept(data);
    }

}
