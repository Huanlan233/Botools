package top.htext.botools.suggestions;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.server.command.ServerCommandSource;
import top.htext.botools.config.BotConfig;
import top.htext.botools.config.BotConfigManager;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class BotSuggestionProvider implements SuggestionProvider<ServerCommandSource> {

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<ServerCommandSource> context, SuggestionsBuilder builder)  {
        try {
            List<BotConfig> botConfigList = BotConfigManager.getBotConfigList(context);

            for ( BotConfig botConfig : botConfigList ) {
                builder.suggest(botConfig.getName()).buildFuture();
            }
            return builder.buildFuture();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
