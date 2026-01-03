import org.jspecify.annotations.NullMarked;

@NullMarked
module net.thenextlvl.interfaces {
    requires com.google.common;
    requires com.google.gson;
    requires net.kyori.adventure.text.minimessage;
    requires net.kyori.examination.api;

    requires static org.jetbrains.annotations;
    requires static org.jspecify;
    requires org.bukkit;
}