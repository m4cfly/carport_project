package app.persistence;

import app.entities.Material;
import app.entities.MaterialVariant;
import app.exceptions.DatabaseException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MaterialMapper {
    public static List<MaterialVariant> getVariantsByProductIdAndMinLength(int minLength, int materialId, ConnectionPool connectionPool) throws DatabaseException
    {
        List<MaterialVariant> materialVariants = new ArrayList<>();
        String sql = "SELECT * FROM material_variant " +
                "INNER JOIN materials m USING(m_id) " +
                "WHERE m_id = ? AND length >= ?";
        try (Connection connection = connectionPool.getConnection())
        {
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setInt(1, materialId);
            ps.setInt(2, minLength);
            ResultSet resultSet = ps.executeQuery();
            while (resultSet.next())
            {
                int materialVariantId = resultSet.getInt("mv_id");
                int material_Id = resultSet.getInt("m_id");
                int length = resultSet.getInt("length");
                String name = resultSet.getString("name");
                String unit = resultSet.getString("unit");
                int price = resultSet.getInt("price");
                Material material = new Material(material_Id, name, price, unit);
                MaterialVariant materialVariant = new MaterialVariant(materialVariantId, material, length);
                materialVariants.add(materialVariant);
            }
        }
        catch (SQLException e)
        {
            throw new DatabaseException("Could not get users from the database", e.getMessage());
        }
        return materialVariants;
    }
}
