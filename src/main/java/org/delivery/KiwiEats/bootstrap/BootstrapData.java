package org.delivery.KiwiEats.bootstrap;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.delivery.KiwiEats.entities.Product;
import org.delivery.KiwiEats.entities.Seller;
import org.delivery.KiwiEats.entities.roles.Privilege;
import org.delivery.KiwiEats.entities.roles.Role;
import org.delivery.KiwiEats.entities.roles.User;
import org.delivery.KiwiEats.repositories.PrivilegeRepository;
import org.delivery.KiwiEats.repositories.RoleRepository;
import org.delivery.KiwiEats.repositories.SellerRepository;
import org.delivery.KiwiEats.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BootstrapData implements CommandLineRunner {

  private final SellerRepository sellerRepository;
  private final RoleRepository roleRepository;
  private final PrivilegeRepository privilegeRepository;
  private final BCryptPasswordEncoder bCryptPasswordEncoder;
  private final UserRepository userRepository;
  private boolean loaded = false;

  @Override
  public void run(String... args) {
    createPrivileges();
    loadSellers();
  }

  private void createPrivileges() {
    if (!loaded) {
      Privilege buyProductPrivilege = privilegeRepository.save(new Privilege("BUY_PRODUCT"));
      Privilege removeProductPrivilege = privilegeRepository.save(new Privilege("REMOVE_PRODUCT"));

      Privilege editProductPrivilege = privilegeRepository.save(new Privilege("EDIT_PRODUCT"));

      Privilege createProductPrivilege = privilegeRepository.save(new Privilege("CREATE_PRODUCT"));

      Privilege deleteProductPrivilege = privilegeRepository.save(new Privilege("DELETE_PRODUCT"));
      Privilege deleteSellerPrivilege = privilegeRepository.save(new Privilege("DELETE_SELLER"));
      Privilege deleteCustomerPrivilege =
          privilegeRepository.save(new Privilege("DELETE_CUSTOMER"));

      List<Privilege> adminPrivileges =
          Arrays.asList(
              editProductPrivilege,
              createProductPrivilege,
              deleteCustomerPrivilege,
              deleteSellerPrivilege,
              deleteProductPrivilege);

      List<Privilege> customerPrivileges =
          Arrays.asList(buyProductPrivilege, removeProductPrivilege);

      List<Privilege> sellerPrivileges =
          Arrays.asList(createProductPrivilege, editProductPrivilege, deleteProductPrivilege);

      Role adminRole = Role.builder().name("ADMIN").privileges(adminPrivileges).build();

      Role customerRole = Role.builder().name("CUSTOMER").privileges(customerPrivileges).build();

      Role sellerRole = Role.builder().name("SELLER").privileges(sellerPrivileges).build();

      roleRepository.save(adminRole);
      roleRepository.save(customerRole);
      roleRepository.save(sellerRole);

      this.loaded = true;
    }
  }

  private void loadSellers() {
    if (sellerRepository.count() < 1) {
      Role seller = roleRepository.findByName("SELLER");

      User mangoUser =
          User.builder()
              .username("Aam Wala")
              .firstName("Mango")
              .middleName("")
              .lastName("Seller")
              .email("mango@lelo.com")
              .password(bCryptPasswordEncoder.encode("mangowala123"))
              .tokenExpired(false)
              .roles(Collections.singleton(seller))
              .build();

      Seller mangoSeller = Seller.builder().earnings(new BigDecimal(100)).build();

      mangoUser.setSeller(mangoSeller);
      mangoSeller.setUser(mangoUser);

      List<Product> mangoProducts =
          List.of(
              Product.builder()
                  .productName("Mango")
                  .productImage("https://i.ibb.co/Vt0mMq3/image.png")
                  .category("FRUIT")
                  .price(new BigDecimal("100"))
                  .build());

      mangoProducts.forEach(product -> product.setSeller(mangoSeller));

      mangoSeller.setProductInStock(mangoProducts);

      sellerRepository.save(mangoSeller);

      Role adminRole = roleRepository.findByName("ADMIN");

      User admin =
          User.builder()
              .username("Administrator")
              .firstName("Test")
              .middleName("")
              .lastName("Admin")
              .email("admin@test.com")
              .password(bCryptPasswordEncoder.encode("admin"))
              .tokenExpired(false)
              .roles(Collections.singleton(adminRole))
              .build();

      userRepository.save(admin);
    }
  }
}
