package com.smart.controllers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.smart.dao.ContactRepository;
import com.smart.dao.PaymentRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.MyOrder;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ContactRepository contactRepository;
    
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    
    @Autowired
    private PaymentRepository paymentRepository;

    @ModelAttribute
    public void addCommonData(Model model, Principal principal) {
        //principal will give current logged in user name
        String userName = principal.getName();
        System.out.println(userName);

        //get user from db
        User user = this.userRepository.getUserByUserName(userName);
        System.out.println(user);
        model.addAttribute("user", user);
    }



    @RequestMapping("/index")
    public String dashboard(Model model) {
        model.addAttribute("title", "User Dashboard");
        return "normal/user_dashboard";
    }

    
    //open add form handler
    @GetMapping("/add-contact")
    public String openAddContactForm(Model model) {
        model.addAttribute("title", "Add Contact");
        model.addAttribute("contact", new Contact());

        return "normal/add_contact";
    }


    @PostMapping("/process-contact")
    public String processForm(@ModelAttribute("contact") Contact contact, @RequestParam("profileImage") MultipartFile file, Principal principal, HttpSession session)

    //The principal represents the user's identity, which can be the username, a user object, or any form of user identification
    {
        //now we store contact details in database
        try {
            String name = principal.getName();
            User user = this.userRepository.getUserByUserName(name);

            if (file.isEmpty()) {} else {
                //upload the file in name in database 
                contact.setImage(file.getOriginalFilename());
                File savefile = new ClassPathResource("static/img").getFile();

                Path path = Paths.get(savefile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
            }
            contact.setUser(user); //this is beacuse bi-direction mapping contact required user vice-versa user required contact so for that first we set user

            user.getContacts().add(contact);
            this.userRepository.save(user);
            session.setAttribute("message", new Message("Added contact successful...", "success"));
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
            session.setAttribute("message", new Message("Something went wrong!!Try again...", "danger"));
        }
        return "normal/add_contact";
    }

    @GetMapping("/show-contact/{page}")
    public String showcontacts(Model m, Principal principal, @PathVariable("page") Integer page) {
        //contact ki list ko bhejni hai
        String UName = principal.getName();
        User user = this.userRepository.getUserByUserName(UName);

        Pageable pageable = PageRequest.of(page, 3);
        Page < Contact > contacts = this.contactRepository.findContactsByUser(user.getId(), pageable);

        m.addAttribute("contacts", contacts);
        m.addAttribute("currentPage", page);
        m.addAttribute("totalPages", contacts.getTotalPages());

        return "normal/show_contact";
    }


    @GetMapping("/contact/{cid}")
    public String showinfo(@PathVariable("cid") Integer cid, Principal principal, Model m) {
        Optional < Contact > contactOptional = this.contactRepository.findById(cid);
        Contact contact = contactOptional.get();

        //we add chenks beacaz only login user can see their own contact details for that we check
        String UName = principal.getName();
        User user = this.userRepository.getUserByUserName(UName);

        if (user.getId() == contact.getUser().getId()) {
            m.addAttribute("contact", contact);
        }
        return "normal/user_info";
    }


    @GetMapping("/delete/{cid}")
    public String Delete(@PathVariable("cid") Integer cid, Model model, HttpSession session, Principal principal) {

        Optional < Contact > contactOptional = this.contactRepository.findById(cid);
        Contact contact = contactOptional.get();

        String UName = principal.getName();
        User user = this.userRepository.getUserByUserName(UName);
        if (user.getId() == contact.getUser().getId()) {
            this.contactRepository.delete(contact);
            session.setAttribute("message", new Message("Contact deleted succesfully...", "danger"));
        }
        return "redirect:/user/show-contact/0";
    }

    //open update form handler
    @PostMapping("/update/{cid}")
    public String updateContact(@PathVariable("cid") Integer cid, Model m) {
        Optional < Contact > contactOptional = this.contactRepository.findById(cid);
        if (contactOptional.isPresent()) {
            Contact contact = contactOptional.get();
            m.addAttribute("contact", contact);
            return "normal/update_form";
        } else {
         
            return "error_page";
        }
    }

    @PostMapping("/process-update")
    public String updateForm(@ModelAttribute("contact") Contact contact, @RequestParam("profileImage") MultipartFile file, Model model, HttpSession session, Principal principal) {
        try {
            Optional<Contact> contactOptional = this.contactRepository.findById(contact.getCid());
            if (contactOptional.isPresent()) {
                Contact oldContactDetail = contactOptional.get();

                if (!file.isEmpty()) {
                    File saveFile = new ClassPathResource("static/img").getFile();
                    Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + file.getOriginalFilename());
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                    contact.setImage(file.getOriginalFilename());
                } else {
                    contact.setImage(oldContactDetail.getImage());
                }

                User user = this.userRepository.getUserByUserName(principal.getName());
                contact.setUser(user);
                this.contactRepository.save(contact);

                session.setAttribute("message", new Message("Update successful...", "success"));
            } else {
                // Handle the case where the contact with the given ID is not found
                session.setAttribute("message", new Message("Contact not found. Update failed.", "danger"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("message", new Message("Update failed. Please try again.", "danger"));
        }
        return "redirect:/user/show-contact/" + contact.getCid();
    }


    @GetMapping("/profile")
    public String showProfile(Model m) {
        m.addAttribute("title", "profile");
        return "normal/profile";
    }
    
    
    @GetMapping("/setting")
    public String ChangePass()
    {
    	return "normal/setting";
    }
    
    @PostMapping("/change-password")
    public String changePasshandler(@RequestParam("oldPassword") String oldPassword,@RequestParam("newPassword") String newPassword,Principal pricipal,HttpSession session )
    {
    	
    	System.out.println("OLD="+oldPassword);
    	System.out.println("NEW="+newPassword);
    	
    	String userName=pricipal.getName();
    	User currentUser=this.userRepository.getUserByUserName(userName);
    	System.out.println(currentUser);
    	
    	if(this.bCryptPasswordEncoder.matches(oldPassword,currentUser.getPassword()))
    	{
    		currentUser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
    		this.userRepository.save(currentUser);
    		session.setAttribute("message", new Message("Password Successfully changed", "success"));
    	}
    	else {
    		session.setAttribute("message", new Message("Wrong old password!", "danger"));
    	}
    	
    	return "redirect:/user/index";
    }
    
    
    @PostMapping("/create-order")
    @ResponseBody
    public String create_order(@RequestBody Map<String,Object> data,Principal principal) throws Exception
    {
    	System.out.print(data);
    	int amt=Integer.parseInt(data.get("amount").toString());   //data come in object format so its convertedd in string and after typecast in integer
    	var client=new RazorpayClient("rzp_test_D9EJdca2UFW3p5","cYeJls1umBNT35CtTpbcoCjf");
    	
    	JSONObject ob=new JSONObject();
    	ob.put("amount", amt*100);
    	ob.put("currency", "INR");
    	ob.put("receipt","txn_23846");
    	
    	
    	
    	//create a new order
    	Order order=client.orders.create(ob);
    	System.out.println(order);
    	
    	
    	//save order details in database
    	
    	MyOrder myOrder=new MyOrder();
    	myOrder.setAmount(order.get("amount")+"");
    	myOrder.setOrderId(order.get("orderId"));
    	myOrder.setPaymentId(null);
    	myOrder.setReceipt(order.get("receipt"));
    	myOrder.setUser(this.userRepository.getUserByUserName(principal.getName()));
    	myOrder.setStatus("created");
    	
    	this.paymentRepository.save(myOrder);
    	return order.toString();
    }
    
    @PostMapping("/update_order")
    public ResponseEntity<?> updatePayment(@RequestBody Map<String,Object> data)
    {
    	MyOrder myorder=this.paymentRepository.findByOrderId(data.get("order_id").toString());
    	
    	myorder.setPaymentId(data.get("payment_id").toString());
    	myorder.setStatus(data.get("status").toString());
    	
    	this.paymentRepository.save(myorder);
		return ResponseEntity.ok(Map.of("msg","updated"));
    	
    }
       
}