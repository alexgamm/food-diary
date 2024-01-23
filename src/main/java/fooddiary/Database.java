package fooddiary;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tech.ydb.table.SessionRetryContext;
import tech.ydb.table.query.DataQueryResult;
import tech.ydb.table.result.ResultSetReader;
import tech.ydb.table.transaction.TxControl;
import tech.ydb.table.query.Params;
import tech.ydb.table.values.PrimitiveValue;

@RequiredArgsConstructor
public class Database {
    private static final Logger log = LoggerFactory.getLogger(Database.class); //@Slf4j
    private final SessionRetryContext retryCtx;
    private void upsertSimple() {
        String query
                = "UPSERT INTO my_foods (series_id, season_id, episode_id, title) "
                + "VALUES (2, 6, 1, \"TBD\");";

        // Begin new transaction with SerializableRW mode
        TxControl<?> txControl = TxControl.serializableRw().setCommitTx(true);

        // Executes data query with specified transaction control settings.
        retryCtx.supplyResult(session -> session.executeDataQuery(query, txControl))
                .join().getValue();
    }
    private void selectSimple() {
        String query
                = "SELECT series_id, title, release_date "
                + "FROM series WHERE series_id = 1;";

        // Begin new transaction with SerializableRW mode
        TxControl<?> txControl = TxControl.serializableRw().setCommitTx(true);

        // Executes data query with specified transaction control settings.
        DataQueryResult result = retryCtx.supplyResult(session -> session.executeDataQuery(query, txControl))
                .join().getValue();



        ResultSetReader rs = result.getResultSet(0);
        while (rs.next()) {
            log.info("read series with id {}, title {} and release_date {}",
                    rs.getColumn("series_id").getUint64(),
                    rs.getColumn("title").getText(),
                    rs.getColumn("release_date").getDate()
            );
        }
    }

    private void selectWithParams(long seriesID, long seasonID) {
        String query
                = "DECLARE $seriesId AS Uint64; "
                + "DECLARE $seasonId AS Uint64; "
                + "SELECT sa.title AS season_title, sr.title AS series_title "
                + "FROM seasons AS sa INNER JOIN series AS sr ON sa.series_id = sr.series_id "
                + "WHERE sa.series_id = $seriesId AND sa.season_id = $seasonId";

        // Begin new transaction with SerializableRW mode
        TxControl<?> txControl = TxControl.serializableRw().setCommitTx(true);

        // Type of parameter values should be exactly the same as in DECLARE statements.
        Params params = Params.of(
                "$seriesId", PrimitiveValue.newUint64(seriesID),
                "$seasonId", PrimitiveValue.newUint64(seasonID)
        );

        DataQueryResult result = retryCtx.supplyResult(session -> session.executeDataQuery(query, txControl, params))
                .join().getValue();


        ResultSetReader rs = result.getResultSet(0);
        while (rs.next()) {
            log.info("read season with title {} for series {}",
                    rs.getColumn("season_title").getText(),
                    rs.getColumn("series_title").getText()
            );
        }
    }

}
