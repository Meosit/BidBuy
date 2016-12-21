package by.mksn.epam.bidbuy.dao.impl;

import by.mksn.epam.bidbuy.dao.LotDAO;
import by.mksn.epam.bidbuy.dao.exception.DAOException;
import by.mksn.epam.bidbuy.dao.pool.ConnectionPool;
import by.mksn.epam.bidbuy.dao.pool.exception.PoolException;
import by.mksn.epam.bidbuy.entity.Lot;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * MySQL implementation of {@link LotDAO}
 */
public class MySqlLotDAO implements LotDAO {

    private static final Logger logger = Logger.getLogger(MySqlLotDAO.class);
    private static final String QUERY_SELECT_BY_ID = "SELECT `id`, `owner_id`, `leader_bid_id`, `auction_type`, `min_price`, `current_price`, `max_price`, `bid_step`, `duration_time`, `name`, `description`, `image_path`, `status`, `status_changed_at`, `created_at`, `modified_at` FROM `lot` WHERE (`id` = ?)";
    private static final String QUERY_SELECT_BY_STATUS = "SELECT `id`, `owner_id`, `leader_bid_id`, `auction_type`, `min_price`, `current_price`, `max_price`, `bid_step`, `duration_time`, `name`, `description`, `image_path`, `status`, `status_changed_at`, `created_at`, `modified_at` FROM `lot` WHERE (`status` = ?)";
    private static final String QUERY_SELECT_BY_TYPE = "SELECT `id`, `owner_id`, `leader_bid_id`, `auction_type`, `min_price`, `current_price`, `max_price`, `bid_step`, `duration_time`, `name`, `description`, `image_path`, `status`, `status_changed_at`, `created_at`, `modified_at` FROM `lot` WHERE (`auction_type` = ?)";
    private static final String QUERY_UPDATE = "UPDATE `lot` SET `owner_id` = ?, `leader_bid_id` = ?, `auction_type` = ?, `min_price` = ?, `current_price` = ?, `max_price` = ?, `bid_step` = ?, `duration_time` = ?, `name` = ?, `description` = ?, `image_path` = ?, `status` = ? WHERE (`id` = ?)";
    private static final String QUERY_INSERT = "INSERT INTO `lot` (`owner_id`, `auction_type`, `min_price`, `current_price`, `max_price`, `bid_step`, `duration_time`, `name`, `description`, `image_path`) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    private static final String QUERY_DELETE = "UPDATE `lot` SET `status` = -1 WHERE `id` = ?";

    @Override
    public Lot selectById(long id) throws DAOException {
        Lot lot;
        try (Connection connection = ConnectionPool.getInstance().getConnection()) {
            lot = selectById(connection, id);
        } catch (SQLException e) {
            throw new DAOException("Cannot execute statement or close connection", e);
        } catch (PoolException e) {
            throw new DAOException("Cannot get connection\n", e);
        }
        return lot;
    }

    @Override
    public List<Lot> selectByStatus(int status) throws DAOException {
        List<Lot> lots;
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(QUERY_SELECT_BY_STATUS)) {
            statement.setLong(1, status);
            lots = parseLotListResultSet(statement);
        } catch (SQLException e) {
            throw new DAOException("Cannot execute statement or close connection", e);
        } catch (PoolException e) {
            throw new DAOException("Cannot get connection\n", e);
        }
        return lots;
    }

    @Override
    public List<Lot> selectByType(int type) throws DAOException {
        List<Lot> lots;
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(QUERY_SELECT_BY_TYPE)) {
            statement.setLong(1, type);
            lots = parseLotListResultSet(statement);
        } catch (SQLException e) {
            throw new DAOException("Cannot execute statement or close connection", e);
        } catch (PoolException e) {
            throw new DAOException("Cannot get connection\n", e);
        }
        return lots;
    }

    @Override
    public void update(Lot updatedLot) throws DAOException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(QUERY_UPDATE)) {
            statement.setLong(1, updatedLot.getOwnerId());
            statement.setLong(2, updatedLot.getLeaderBetId());
            statement.setInt(3, updatedLot.getAuctionType());
            statement.setBigDecimal(4, updatedLot.getMinPrice());
            statement.setBigDecimal(5, updatedLot.getCurrentPrice());
            statement.setBigDecimal(6, updatedLot.getMaxPrice());
            statement.setBigDecimal(7, updatedLot.getBidStep());
            statement.setLong(8, updatedLot.getDurationTime());
            statement.setString(9, updatedLot.getName());
            statement.setString(10, updatedLot.getDescription());
            statement.setString(11, updatedLot.getImagePath());
            statement.setInt(12, updatedLot.getStatus());

            statement.setLong(13, updatedLot.getId());

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Cannot execute statement or close connection", e);
        } catch (PoolException e) {
            throw new DAOException("Cannot get connection\n", e);
        }
    }

    @Override
    public void delete(long id) throws DAOException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(QUERY_DELETE)) {
            statement.setLong(1, id);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Cannot execute statement or close connection", e);
        } catch (PoolException e) {
            throw new DAOException("Cannot get connection\n", e);
        }
    }

    @Override
    public void insert(String name, String description, long ownerId, int auctionType, BigDecimal minPrice, BigDecimal maxPrice, BigDecimal bidStep, int durationTime, String imagePath) throws DAOException {
        try (Connection connection = ConnectionPool.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(QUERY_INSERT)) {

            statement.setLong(1, ownerId);
            statement.setInt(2, auctionType);
            statement.setBigDecimal(3, minPrice);
            switch (auctionType) {
                case Lot.TYPE_ENGLISH:
                case Lot.TYPE_LOTTERY:
                    statement.setBigDecimal(4, minPrice);
                    break;
                case Lot.TYPE_REVERSIVE:
                    statement.setBigDecimal(4, maxPrice);
                    break;
                default:
                    logger.error("Cannot resolve auction type (" + auctionType + ")");
                    throw new DAOException("Cannot resolve auction type");
            }
            statement.setBigDecimal(5, maxPrice);
            statement.setBigDecimal(6, bidStep);
            statement.setLong(7, durationTime);
            statement.setString(8, name);
            statement.setString(9, description);
            statement.setString(10, imagePath);

            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Cannot execute statement or close connection", e);
        } catch (PoolException e) {
            throw new DAOException("Cannot get connection\n", e);
        }
    }

    private Lot selectById(Connection connection, long id) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(QUERY_SELECT_BY_ID)) {
            statement.setLong(1, id);
            return parseLotResultSet(statement);
        }
    }

    private Lot parseLotResultSet(PreparedStatement statement) throws SQLException {
        Lot lot;
        try (ResultSet resultSet = statement.executeQuery()) {
            if (resultSet.next()) {
                lot = new Lot();
                lot.setId(resultSet.getLong(1));
                lot.setOwnerId(resultSet.getLong(2));
                lot.setLeaderBetId(resultSet.getLong(3));
                lot.setAuctionType(resultSet.getInt(4));
                lot.setMinPrice(resultSet.getBigDecimal(5));
                lot.setCurrentPrice(resultSet.getBigDecimal(6));
                lot.setMaxPrice(resultSet.getBigDecimal(7));
                lot.setBidStep(resultSet.getBigDecimal(8));
                lot.setDurationTime(resultSet.getLong(9));
                lot.setName(resultSet.getString(10));
                lot.setDescription(resultSet.getString(11));
                lot.setImagePath(resultSet.getString(12));
                lot.setStatus(resultSet.getInt(13));
                lot.setStatusChangedAt(resultSet.getTimestamp(14));
                lot.setCreatedAt(resultSet.getTimestamp(15));
                lot.setModifiedAt(resultSet.getTimestamp(16));
            } else {
                lot = null;
            }
        }
        return lot;
    }

    private List<Lot> parseLotListResultSet(PreparedStatement statement) throws SQLException {
        LinkedList<Lot> lots = new LinkedList<>();
        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Lot lot = new Lot();
                lot.setId(resultSet.getLong(1));
                lot.setOwnerId(resultSet.getLong(2));
                lot.setLeaderBetId(resultSet.getLong(3));
                lot.setAuctionType(resultSet.getInt(4));
                lot.setMinPrice(resultSet.getBigDecimal(5));
                lot.setCurrentPrice(resultSet.getBigDecimal(6));
                lot.setMaxPrice(resultSet.getBigDecimal(7));
                lot.setBidStep(resultSet.getBigDecimal(8));
                lot.setDurationTime(resultSet.getLong(9));
                lot.setName(resultSet.getString(10));
                lot.setDescription(resultSet.getString(11));
                lot.setImagePath(resultSet.getString(12));
                lot.setStatus(resultSet.getInt(13));
                lot.setStatusChangedAt(resultSet.getTimestamp(14));
                lot.setCreatedAt(resultSet.getTimestamp(15));
                lot.setModifiedAt(resultSet.getTimestamp(16));

                lots.add(lot);
            }
        }
        return lots;
    }
}