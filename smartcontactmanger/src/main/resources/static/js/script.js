const toggleSidebar = () =>{

    if($(".sidebar").is(":visible"))
    {
        $(".sidebar").css("display","none");
        $(".content").css("margin-left","0%");
    }
    else{
        
        $(".sidebar").css("display","block");
        $(".content").css("margin-left","20%");
    }
};

const search= () =>{
    let query=$("#search-input").val();

    if(query=='')
    {
        $(".search-result").hide();
    }
    else{
        let url=`http://localhost:8282/search/${query}`;     //this is api
        fetch(url)
        .then((response)=>{
            return response.json();
        })
        .then((data)=>{
            console.log(data);

            let text=`<div class='list-group'>`;
            data.forEach((contact) => {
                text+= `<a href='/user/contact/+${contact.cid}' class='list-group-item list-group-action'> ${contact.name}</a>`;                
            });

            text+=`</div>`;

            $(".search-result").html(text);
            $(".search-result").show();
        });
    }
}



//first request to server  to create order
const paymentStart=() =>{
    console.log("payment started..");
    let amount=$("#payment").val();
    if(amount=="" || amount==null)
    {
            alert("amount is required!!");
            return;
    }


//we will use ajax to send request to server to create order 

$.ajax(
    {
        url: "/user/create-order",
        data:JSON.stringify({amount:amount,info:"order_request"}),
        contentType:"application/json",
        type:"POST",
        dataType:"json",
        success:function(response)
        {
            console.log(response); 
            if(response.status=="created")
            {
				let options={
                    key:'',
                    amount:response.amount,
                    currency:"INR",
                    name:"Smart Contact Manager",
                    description:"Donation",
                    image:"",
                    order_id:response.id,
                    handler:function(response)
                    {
                        console.log(response.razorpay_payment_id);
                        console.log(response.razorpay_order_id);
                        console.log(response.razorpay_sinature);

                        updatePaymentOnServer(
                            response.razorpay_payment_id,
                            response.razorpay_order_id,
                            "paid"
                        );
                        swal("Good job","payment successful !!","success");   
                    },
                    prefill: {
                        name: "",
                        email: "",
                        contact: ""
                        },
                        notes: {
                        address: "Learn with Nitin",
                        },
                        theme: {
                        color: "#3399cc",
                        },
                };
                let rzp=new Razorpay(options);
                rzp.on('payment.failed', function (response){
                    console.log(response.error.code);
                    console.log(response.error.description);
                    console.log(response.error.source);
                    console.log(response.error.step);
                    console.log(response.error.reason);
                    console.log(response.error.metadata.order_id);
                    console.log(response.error.metadata.payment_id);
                    swal("oops payment failed!!");
                    });
                rzp.open();

			}
        },
        error:function(error){
            console.log(error);
            alert("something went error!!");
        },

    });
};

function updatePaymentOnServer(payment_id,order_id,status)
{
    $.ajax(
        {
            url: "/user/update_order",
            data:JSON.stringify({payment_id:payment_id,order_id:order_id,status:status,}),
            contentType:"application/json",
            type:"POST",
            dataType:"json",
            success:function(response)
            {
                swal("Good job","payment successful !!","success"); 
            },
            error:function(error)
            {
                swal("Failed","your payment is successful,but we cant get on server","we will contact you");  
            },
});
}

function registerUser()
{
	event.preventDefault();
	
	var name=$("#name_field").val();
	var email=$("#email_field").val();
	var password=$("#password_field").val();
	var description=$("#desc_field").val();
	
	 if (!name) {
    alert("Name field is required.");
    return false;
  }

  if (!email) {
    alert("Email field is required.");
    return false;
  }


  if (!/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(email)) {
    alert("Invalid email format.");
    return false;
  }
	
	$.ajax({
		type:'POST',
		data:{name: name,email:email,password:password,description:description},
		url:'/do-register',
		success:function()
		{
			alert("Regitered successfully...");
			window.location.href="/login";
		},
		 error: function() {
            alert("Registration failed. Please try again.");
        }
	})
	
}



function loginSubmit() {
    event.preventDefault();

    var username = $("#email").val();
    var password = $("#password").val();
    $.ajax({
        type: 'POST',
        data: { username: username, password: password },
        url: '/dologin',
        success: function(logged) {
            $("#loginresult").html(logged);
            alert("Login Successful"); // Show an alert instead of the modal
            window.location.href = "/user/index";
        },
        error: function() {
            alert("Login failed. Please try again.");
        }
    });
}



