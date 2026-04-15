package github.betot.trollblock.event;

import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ClientData {

    // UUID du réseau sélectionné (comme Create "link")
    public static UUID selectedLink;

    // Cache des blocs à afficher en outline
    public static final List<BlockPos> cached = new ArrayList<>();
}