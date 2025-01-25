package com.artillexstudios.axvanish.database;

import com.artillexstudios.axapi.utils.AsyncUtils;
import com.artillexstudios.axvanish.config.Config;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public final class DataHandler {

    public static CompletionStage<Void> setup() {
        ArrayList<CompletableFuture<Integer>> futures = new ArrayList<>();

//        CompletionStage<Integer> teams = DatabaseConnector.getInstance().context().createTableIfNotExists(TEAMS)
//                .column(ID, SQLDataType.INTEGER.identity(true))
//                .column(TEAM_NAME, SQLDataType.VARCHAR)
//                .column(TEAM_LEADER, SQLDataType.INTEGER)
//                .primaryKey(ID)
//                .executeAsync(AsyncUtils.executor())
//                .exceptionallyAsync(throwable -> {
//                    LogUtils.error("An unexpected error occurred while running teams table creation query!", throwable);
//                    return 0;
//                });
//
//        futures.add(teams.toCompletableFuture());


        if (Config.database.type == DatabaseType.SQLITE) {
            CompletableFuture<Integer> pragma = new CompletableFuture<>();
            AsyncUtils.executor().submit(() -> {
                DatabaseConnector.getInstance().context().fetch("PRAGMA journal_mode=WAL;");
                DatabaseConnector.getInstance().context().execute("PRAGMA synchronous = off;");
                DatabaseConnector.getInstance().context().execute("PRAGMA page_size = 32768;");
                DatabaseConnector.getInstance().context().fetch("PRAGMA mmap_size = 30000000000;");
                pragma.complete(1);
            });
            futures.add(pragma);
        }

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }
}
