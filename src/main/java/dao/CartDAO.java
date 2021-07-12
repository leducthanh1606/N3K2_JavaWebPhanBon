/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.List;
import model.Cart;
import model.User;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import model.Order;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.servlet.ModelAndView;

/**
 *
 * @author Dell 7450
 */
public class CartDAO {

    JdbcTemplate template;

    public void setTemplate(JdbcTemplate template) {
        this.template = template;
    }

    //gio hang
    public List<Cart> AllCart(String phone) {
        String sql = "select * from cart where phone=?";
        return template.query(sql, new Object[]{phone}, new BeanPropertyRowMapper<>(Cart.class));
    }
    public List<Cart> AllCart() {
        String sql = "select * from cart where ";
        return template.query(sql, new Object[]{}, new BeanPropertyRowMapper<>(Cart.class));
    }

    public void ThemCart(Cart cart) {
        int Idexist = IsExit(cart.getPhone(), cart.getName(), cart.getSpecifications());
        if (Idexist != 0) {
            Cart product = searchCart(cart.getPhone(), cart.getName(), cart.getSpecifications());
            int sl = product.getAmount() + 1;
            UpdateCart(sl, product.getId());
        } else {
            String sql = String.format("insert into cart (phone,name, specifications,pic, price, amount) values('%s','%s','%d','%s','%f','%d')",
                    cart.getPhone(), cart.getName(), cart.getSpecifications(), cart.getPic(), cart.getPrice(), cart.getAmount());
            template.update(sql);
        }
    }

    //kiểm tra giỏ hàng của người dùng có sp hay chưa
    public int IsExit(String phone, String name, int specifications) {
        String sql = "select count(*) from cart where specifications=? and name = ? and phone = ?";
        int temp = template.queryForObject(sql, new Object[]{specifications, name, phone}, Integer.class);
        return temp;
    }

    //lấy thông tin của sp nếu tồn tại 
    public Cart searchCart(String phone, String name, int specifications) {
        String sql = "select * from cart where specifications=? and name = ? and phone = ?";
        Cart temp = template.queryForObject(sql, new Object[]{specifications, name, phone}, new BeanPropertyRowMapper<>(Cart.class));
        return temp;
    }

    public Cart searchCart(int id) {
        String sql = "select * from cart where id=?";
        return template.queryForObject(sql, new Object[]{id}, new BeanPropertyRowMapper<>(Cart.class));
    }

    public List<Cart> searchCart(String phone) {
        String sql = "select * from cart where phone=?";
        return template.query(sql, new Object[]{phone}, new BeanPropertyRowMapper<>(Cart.class));
    }

    public int UpdateCart(int sl, int id) {
        if (sl == 0) {
            return Delete(id);
        } else {
            String sub = "update cart set amount=? where id=?";
            return template.update(sub, sl, id);
        }

    }

    //Cập nhât giỏ hàng khi tăng giảm số lượng
    public int Sub(int id) {
        Cart c = searchCart(id);
        int sl = c.getAmount() - 1;
        return UpdateCart(sl, id);
    }

    public int Add(int id) {
        Cart c = searchCart(id);
        int sl = c.getAmount() + 1;
        return UpdateCart(sl, id);
    }

    public int Delete(int id) {
        String sql = "delete from cart where id = ?";
        return template.update(sql, id);
    }

    public User Search_User(String phone) {
        String sql = "select * from users where phone = ?";
        return template.queryForObject(sql, new Object[]{phone}, new BeanPropertyRowMapper<>(User.class));
    }

    public void Buy(float totalmoney, String phone) {
        float congno = -1000000;
        User u = Search_User(phone);
        if (u.getId() != 0) {
            float newmoney = (float) (u.getMoney() - totalmoney);
            if (newmoney < congno) {
                JFrame frame = new JFrame("Swing Tester");
                JOptionPane.showMessageDialog(frame,
                        "Tài Khoản Nợ Vượt Quá Mức", "",
                        JOptionPane.INFORMATION_MESSAGE);
                ModelAndView v = new ModelAndView("book/listSach");
            } else {
                //cập nhật lại số tiền của khách
                String sub = "update users set money=? where phone=?";
                template.update(sub, newmoney, phone);
                //lưu đơn hàng vừa mua xuống order
                List<Cart> clist = searchCart(phone);
                for (Cart cart : clist) {
                    if (cart.getId() != 0) {
                        //lấy ngày hiện tại
                        LocalDate day = LocalDate.now();
                        Order o = new Order();
                        o.setNameuser(u.getName());
                        o.setPhone(u.getPhone());
                        o.setNameproduct(cart.getName());
                        o.setPriceproduct(cart.getPrice());
                        o.setAmount(cart.getAmount());
                        o.setTotal(totalmoney);
                        o.setDay(day);
                        ThemOrder(o);
                    }
                }
                DeleCart(phone);
            }
        }

    }

    public void DeleCart(String phone) {
        String sql = "delete from cart where phone = ?";
        template.update(sql, phone);
    }

    //thêm vào order
    public void ThemOrder(Order o) {
        String sql = String.format("insert into orders (nameuser,phone,nameproduct, priceproduct, amount, total,day) values('%s','%s','%s','%f','%d','%f','%s')",
                o.getNameuser(), o.getPhone(), o.getNameproduct(), o.getPriceproduct(), o.getAmount(), o.getTotal(), o.getDay());
        template.update(sql);

    }
     public List<Order> AllOrder(String phone) {
        String sql = "select * from orders where phone = ?";
        return template.query(sql, new Object[]{phone}, new BeanPropertyRowMapper<>(Order.class));
    }
    public List<Order> AllOrder() {
        String sql = "select * from orders";
        return template.query(sql, new Object[]{}, new BeanPropertyRowMapper<>(Order.class));
    }

}
