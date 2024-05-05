package com.nhom14.webbookstore.controller.customer;

import com.nhom14.webbookstore.entity.*;
import com.nhom14.webbookstore.service.*;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class AccountAddressController {
    private AccountAddressService accountAddressService;
    private AccountService accountService;
    private CityService cityService;
    private DistrictService districtService;
    private WardService wardService;

    @Autowired
    public AccountAddressController(AccountAddressService accountAddressService, AccountService accountService, CityService cityService, DistrictService districtService, WardService wardService) {
        super();
        this.accountAddressService = accountAddressService;
        this.accountService = accountService;
        this.cityService = cityService;
        this.districtService = districtService;
        this.wardService = wardService;
    }

    @GetMapping("/viewaddresses")
    public String viewAddresses(Model model, HttpSession session) {
        // Kiểm tra xem người dùng đã đăng nhập hay chưa
        Account account = (Account) session.getAttribute("account");
        if (account == null) {
            return "redirect:/customer/loginaccount";
        }

        // Lấy danh sách địa chỉ của người dùng
        List<AccountAddress> addresses = accountAddressService.getAddressesByAccount(account);

        // Thêm danh sách địa chỉ vào model
        model.addAttribute("addresses", addresses);

        return "customer/viewaddresses";
    }

    @GetMapping("/addaddress")
    public String showAddAccountAddressForm(Model model, HttpSession session) {
        Account account = (Account) session.getAttribute("account");

        // Kiểm tra xem người dùng đã đăng nhập hay chưa
        if (account == null) {
            // Nếu chưa đăng nhập, chuyển hướng về trang đăng nhập
            return "redirect:/customer/loginaccount";
        }

        List<City> cities = cityService.getAllCities();

        // Lấy thông tin tài khoản để hiển thị trên form
        model.addAttribute("account", account);
        model.addAttribute("cities", cities);
        return "customer/addaddress";
    }

    @PostMapping("/addaddress")
    public String addAccountAddress(@RequestParam("recipientName") String recipientName,
                                    @RequestParam("phoneNumber") String phoneNumber,
                                    @RequestParam("city") int cityId,
                                    @RequestParam("district") int districtId,
                                    @RequestParam("ward") int wardId,
                                    @RequestParam("addressDetails") String addressDetails,
                                    @RequestParam("addressNote") String addressNote,
                                    @RequestParam("addressType") int addressType,
                                    @RequestParam("isDefault") int isDefault,
                                    HttpSession session,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        // Kiểm tra xem người dùng đã đăng nhập hay chưa
        Account account = (Account) session.getAttribute("account");
        if (account == null) {
            return "redirect:/customer/loginaccount";
        }

        // Lấy city, district, ward theo id
        City city = cityService.getCityById(cityId);
        District district = districtService.getDistrictById(districtId);
        Ward ward = wardService.getWardById(wardId);

        // Tạo object AccountAddress
        AccountAddress accountAddress = new AccountAddress();
        accountAddress.setRecipientName(recipientName);
        accountAddress.setPhoneNumber(phoneNumber);
        accountAddress.setCity(city);
        accountAddress.setDistrict(district);
        accountAddress.setWard(ward);
        accountAddress.setAddressDetails(addressDetails);
        accountAddress.setAddressNote(addressNote);
        accountAddress.setAddressType(addressType);
        accountAddress.setIsDefault(isDefault);
        accountAddress.setAccount(account);

        // Nếu địa chỉ mới là địa chỉ mặc định
        if (isDefault == 1) {
            // Tìm địa chỉ mặc định hiện có
            AccountAddress currentDefaultAddress = accountAddressService.getDefaultAddress(account);

            // Nếu tìm thấy địa chỉ mặc định hiện có
            if (currentDefaultAddress != null) {
                // Cập nhật địa chỉ mặc định hiện có thành không mặc định
                currentDefaultAddress.setIsDefault(0);
                accountAddressService.updateAccountAddress(currentDefaultAddress);
            }

            // Tạo địa chỉ đầy đủ
            String fullAddress = addressDetails + ", " + ward.getName() + ", " + district.getName() + ", " + city.getName();

            // Cập nhật địa chỉ cho account
            account.setAddress(fullAddress);
            accountService.updateAccount(account);
        }

        // Thêm địa chỉ mới
        accountAddressService.addAccountAddress(accountAddress);

        // Thêm thông báo thành công
        redirectAttributes.addFlashAttribute("message", "Thêm địa chỉ thành công!");

        return "redirect:/viewaddresses";
    }

    @GetMapping("/updateaddress/{id}")
    public String showUpdateAddressForm(@PathVariable("id") int id, Model model,
                                        RedirectAttributes redirectAttributes,
                                        HttpSession session) {
        // Kiểm tra xem người dùng đã đăng nhập hay chưa
        Account account = (Account) session.getAttribute("account");
        if (account == null) {
            return "redirect:/customer/loginaccount";
        }

        // Tìm địa chỉ bằng ID
        AccountAddress address = accountAddressService.getAccountAddressById(id);

        // Nếu không tìm thấy địa chỉ, chuyển hướng người dùng về trang danh sách địa chỉ
        if (address == null) {
            // Thêm thông báo lỗi
            redirectAttributes.addFlashAttribute("message", "Địa chỉ không tồn tại!");
            return "redirect:/viewaddresses";
        }

        List<City> cities = cityService.getAllCities();

        // Thêm địa chỉ vào model
        model.addAttribute("address", address);
        model.addAttribute("cities", cities);
        return "customer/updateaddress";
    }


    @PostMapping("/updateaddress")
    public String updateAccountAddress(@RequestParam("id") int id,
                                       @RequestParam("recipientName") String recipientName,
                                       @RequestParam("phoneNumber") String phoneNumber,
                                       @RequestParam("city") int cityId,
                                       @RequestParam("district") int districtId,
                                       @RequestParam("ward") int wardId,
                                       @RequestParam("addressDetails") String addressDetails,
                                       @RequestParam("addressNote") String addressNote,
                                       @RequestParam("addressType") int addressType,
                                       @RequestParam(value = "isDefault", required = false) Integer isDefault,
                                       HttpSession session,
                                       RedirectAttributes redirectAttributes) {
        // Kiểm tra xem người dùng đã đăng nhập hay chưa
        Account account = (Account) session.getAttribute("account");
        if (account == null) {
            return "redirect:/customer/loginaccount";
        }

        // Lấy city, district, ward theo id
        City city = cityService.getCityById(cityId);
        District district = districtService.getDistrictById(districtId);
        Ward ward = wardService.getWardById(wardId);

        // Lấy địa chỉ cần cập nhật
        AccountAddress accountAddress = accountAddressService.getAccountAddressById(id);
        if (accountAddress == null) {
            // Thêm thông báo lỗi
            redirectAttributes.addFlashAttribute("message", "Địa chỉ không tồn tại!");
            return "redirect:/viewaddresses";
        }

        // Cập nhật thông tin địa chỉ
        accountAddress.setRecipientName(recipientName);
        accountAddress.setPhoneNumber(phoneNumber);
        accountAddress.setCity(city);
        accountAddress.setDistrict(district);
        accountAddress.setWard(ward);
        accountAddress.setAddressDetails(addressDetails);
        accountAddress.setAddressNote(addressNote);
        accountAddress.setAddressType(addressType);

        // Kiểm tra nếu isDefault không null thì cập nhật giá trị
        if (isDefault != null) {
            accountAddress.setIsDefault(isDefault);
        }

        // Nếu địa chỉ mới là địa chỉ mặc định
        if (isDefault != null && isDefault == 1) {
            // Tìm địa chỉ mặc định hiện có
            AccountAddress currentDefaultAddress = accountAddressService.getDefaultAddress(account);

            // Nếu tìm thấy địa chỉ mặc định hiện có
            if (currentDefaultAddress != null && currentDefaultAddress.getId() != id) {
                // Cập nhật địa chỉ mặc định hiện có thành không mặc định
                currentDefaultAddress.setIsDefault(0);
                accountAddressService.updateAccountAddress(currentDefaultAddress);
            }

            // Tạo địa chỉ đầy đủ
            String fullAddress = addressDetails + ", " + ward.getName() + ", " + district.getName() + ", " + city.getName();

            // Cập nhật địa chỉ cho account
            account.setAddress(fullAddress);
            accountService.updateAccount(account);
        }

        // Cập nhật địa chỉ
        accountAddressService.updateAccountAddress(accountAddress);

        // Thêm thông báo thành công
        redirectAttributes.addFlashAttribute("message", "Cập nhật địa chỉ thành công!");

        return "redirect:/viewaddresses";
    }


    @PostMapping("/deleteaddress")
    public ResponseEntity<String> deleteAddress(@RequestParam("addressId") Integer addressId,
                                                HttpSession session) {
        Account account = (Account) session.getAttribute("account");

        // Kiểm tra xem người dùng đã đăng nhập hay chưa
        if (account == null) {
            // Nếu chưa đăng nhập, trả về thông báo lỗi
            String message = "Bạn cần đăng nhập để thêm sản phẩm vào giỏ hàng";
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(message);
        }

        AccountAddress address = accountAddressService.getAccountAddressById(addressId);
        if (address.getIsDefault() == 1) {
            // Nếu là địa chỉ mặc định thì không cho xóa
            return new ResponseEntity<>("Không thể xóa địa chỉ mặc định", HttpStatus.BAD_REQUEST);
        }

        try {
            accountAddressService.deleteAccountAddress(addressId);
            return new ResponseEntity<>("Địa chỉ đã được xóa thành công", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Đã xảy ra lỗi khi xóa địa chỉ", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
