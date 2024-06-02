package app.persistence;

import app.entities.*;
import app.exceptions.DatabaseException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


    public class OrderMapper {
        public static List<Order> getAllOrders(ConnectionPool connectionPool) throws DatabaseException
        {
            List<Order> orderList = new ArrayList<>();
            String sql = "SELECT * FROM orders inner join users using(user_id) ORDER BY order_id DESC";
            try (
                    Connection connection = connectionPool.getConnection();
                    var prepareStatement = connection.prepareStatement(sql);
                    var resultSet = prepareStatement.executeQuery()
            )
            {
                while (resultSet.next())
                {
                    int userId = resultSet.getInt("user_id");
                    String userName = resultSet.getString("user_name");
                    String password = resultSet.getString("user_password");
                    int balance = resultSet.getInt("user_balance");
                    String role = resultSet.getString("user_role");
                    int orderId = resultSet.getInt("order_id");
                    int carportWidth = resultSet.getInt("width");
                    int carportLength = resultSet.getInt("length");
                    int status = resultSet.getInt("status");
                    int totalPrice = resultSet.getInt("total_price");
                    User user = new User(userId, userName, password, balance, role);
                    Order order = new Order(orderId, status, carportWidth, carportLength, totalPrice, user);
                    orderList.add(order);
                }
            }
            catch (SQLException e)
            {
                throw new DatabaseException("Could not get all the orders from the database", e.getMessage());
            }
            return orderList;
        }

        public static List<Material_Item> getMaterialItemsByOrderId(int orderId, ConnectionPool connectionPool) throws DatabaseException
        {
            List<Material_Item> materialItemList = new ArrayList<>();
            String sql = "SELECT * FROM bill_of_materials_view where order_id = ?";
            try (
                    Connection connection = connectionPool.getConnection();
                    PreparedStatement prepareStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            )
            {
                prepareStatement.setInt(1, orderId);
                var rs = prepareStatement.executeQuery();
                while (rs.next())
                {
                    // Order
                    int carportWidth = rs.getInt("width");
                    int carportLength = rs.getInt("length");
                    int status = rs.getInt("status");
                    int totalPrice = rs.getInt("total_price");
                    Order order = new Order(orderId, status, carportWidth, carportLength, totalPrice, null);
                    //Material
                    int materialId = rs.getInt("m_id");
                    String name = rs.getString("name");
                    String unit = rs.getString("unit");
                    int price = rs.getInt("price");
                    Material material = new Material(materialId, name, price, unit);
                    // Material variant
                    int materialVariantId = rs.getInt("mv_id");
                    int mvlength = rs.getInt("mvlength");
                    MaterialVariant materialVariant = new MaterialVariant(materialVariantId, material, mvlength);
                    // MaterialItem
                    int materialItemId = rs.getInt("m_item_id");
                    int quantity = rs.getInt("quantity");
                    String description = rs.getString("describable");
                    Material_Item materialItem = new Material_Item(materialItemId, quantity, description, order, materialVariant);
                    materialItemList.add(materialItem);
                }
            }
            catch (SQLException e)
            {
                throw new DatabaseException("Could not get All data of BOM from the database", e.getMessage());
            }
            return materialItemList;
        }

        public static Order insertOrder(Order order, ConnectionPool connectionPool) throws DatabaseException
        {
            String sql = "INSERT INTO orders (width, length, status, user_id, total_price) " +
                    "VALUES (?, ?, ?, ?, ?)";
            try (Connection connection = connectionPool.getConnection())
            {
                try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
                {
                    ps.setInt(1, order.getWidth());
                    ps.setInt(2, order.getLength());
                    ps.setInt(3, 1);
                    ps.setInt(4, order.getUser().getUserId());
                    ps.setInt(5, order.getTotalPrice());
                    ps.executeUpdate();
                    ResultSet keySet = ps.getGeneratedKeys();
                    if (keySet.next())
                    {
                        Order newOrder = new Order(keySet.getInt(1), order.getLength(), order.getWidth(), order.getTotalPrice(), 1, order.getUser());
                        return newOrder;
                    } else
                        return null;
                }
            }
            catch (SQLException e)
            {
                throw new DatabaseException("Could not insert the order in the database", e.getMessage());
            }

        }

        public static void insertMaterialItems(List<Material_Item> materialItems, ConnectionPool connectionPool) throws DatabaseException
        {
            String sql = "INSERT INTO material_item (orders_id, mv_id, quantity, describable) " +
                    "VALUES (?, ?, ?, ?)";

            try (Connection connection = connectionPool.getConnection()) {

                for (Material_Item materialItem : materialItems) {
                    try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                        ps.setInt(1, materialItem.getOrder().getOrderID());
                        ps.setInt(2, materialItem.getMaterialVariant().getMaterialVariantID());
                        ps.setInt(3, materialItem.getQuantity());
                        ps.setString(4, materialItem.getDescription());


                        ps.executeUpdate();
                        ResultSet keySet = ps.getGeneratedKeys();
                        if (keySet.next()) {
                            Material_Item materialItem1 = new Material_Item(keySet.getInt(1), materialItem.getQuantity(), materialItem.getDescription(), materialItem.getOrder().getOrderID(), materialItem.getMaterialVariant().getMaterialVariantID());


                        }

                    }
                }
            }
            catch (SQLException e)
            {
                throw new DatabaseException("Could not create orderitem in the database", e.getMessage());
            }
        }

        public static List<Order> viewShoppingCart(int userId, ConnectionPool connectionPool) throws DatabaseException
        {
            List<Order> orderList = new ArrayList<>();
            String sql = "SELECT * FROM orders inner join users using(user_id)";

            try (
                    Connection connection = connectionPool.getConnection();
                    PreparedStatement ps = connection.prepareStatement(sql)
            )
            {
                ps.setInt(1, userId);
                ResultSet rs = ps.executeQuery();
                while (rs.next())
                {
                    int orderId = rs.getInt("order_id");
                    int carportWidth = rs.getInt("width");
                    int carportLength = rs.getInt("length");
                    int totalPrice = rs.getInt("total_price");
                    //int userId = rs.getInt("user_id");
                    String userName = rs.getString("user_name");
                    String userPassword = rs.getString("user_password");
                    String userRole = rs.getString("user_role");
                    int userBalance = rs.getInt("user_balance");

                    orderList.add(new Order(orderId, carportLength, carportWidth, totalPrice, 1, new User(userId, userName, userPassword, userBalance, userRole)));

                }
            }
            catch (SQLException e)
            {
                throw new DatabaseException("Something went wrong with the database", e.getMessage());
            }
            return orderList;
        }
//    public static List<Task> viewAllOrders(int userId, ConnectionPool connectionPool) throws DatabaseException
//    {
//        List<Task> taskList = new ArrayList<>();
//        String sql = "select * from task where user_id=? order by name";
//
//        try (
//                Connection connection = connectionPool.getConnection();
//                PreparedStatement ps = connection.prepareStatement(sql)
//        )
//        {
//            ps.setInt(1, userId);
//            ResultSet rs = ps.executeQuery();
//            while (rs.next())
//            {
//                int id = rs.getInt("task_id");
//                String name = rs.getString("name");
//                Boolean done = rs.getBoolean("done");
//                taskList.add(new Task(id, name, done, userId));
//            }
//        }
//        catch (SQLException e)
//        {
//            throw new DatabaseException("Fejl!!!!", e.getMessage());
//        }
//        return taskList;
//    }
//
//    public static Task saveOrder(User user, String taskName, ConnectionPool connectionPool) throws DatabaseException
//    {
//        Task newTask = null;
//
//        String sql = "insert into task (name, done, user_id) values (?,?,?)";
//
//        try (
//                Connection connection = connectionPool.getConnection();
//                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)
//        )
//        {
//            ps.setString(1, taskName);
//            ps.setBoolean(2, false);
//            ps.setInt(3, user.getUserId());
//            int rowsAffected = ps.executeUpdate();
//            if (rowsAffected == 1)
//            {
//                ResultSet rs = ps.getGeneratedKeys();
//                rs.next();
//                int newId = rs.getInt(1);
//                newTask = new Task(newId, taskName, false, user.getUserId());
//            } else
//            {
//                throw new DatabaseException("Fejl under indsætning af task: " + taskName);
//            }
//        }
//        catch (SQLException e)
//        {
//            throw new DatabaseException("Fejl i DB connection", e.getMessage());
//        }
//        return newTask;
//    }

        public static void payForOrder(Order newOrder, int totalPrice, int userId, ConnectionPool connectionPool) throws DatabaseException
        {
            String sql = "UPDATE public.users SET user_balance = user_balance - ? WHERE user_id=?;";


            try (
                    Connection connection = connectionPool.getConnection();
                    PreparedStatement ps = connection.prepareStatement(sql)
            )
            {
                ps.setInt(1, totalPrice);
                ps.setInt(2, userId);

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected != 1)
                {
                    throw new DatabaseException("The transaction did not go through");
                }

            }
            catch (SQLException e)
            {
                throw new DatabaseException("Error while performing transaction");
            }
        }


        public static int calculatePrice(int orderId, ConnectionPool connectionPool) throws DatabaseException {

            String sql = "UPDATE public.orders SET total_price = ? WHERE order_id = ?;";


            try (
                    Connection connection = connectionPool.getConnection();
                    PreparedStatement ps = connection.prepareStatement(sql)
            )
            {
                List<Material_Item> materialItems = getMaterialItemsByOrderId(orderId, connectionPool);

                int totalPrice = 0;

                for (Material_Item materialItem : materialItems) {
                   totalPrice = totalPrice + (materialItem.getMaterialVariant().getMaterial().getPrice() * materialItem.getQuantity());
                }

                ps.setInt(2, orderId);
                ps.setInt(1, totalPrice);

                int rowsAffected = ps.executeUpdate();
                if (rowsAffected != 1)
                {
                    throw new DatabaseException("The price calculation did not succeed");
                }

                return totalPrice;
            }
            catch (SQLException e)
            {
                throw new DatabaseException("Error while performing price calculation");
            }
        }

/*        public static List<Order> getAllOrdersPerUser(int userId, ConnectionPool connectionPool) {

        }

*/
    }






