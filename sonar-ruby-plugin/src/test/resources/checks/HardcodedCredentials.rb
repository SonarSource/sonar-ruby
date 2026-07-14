x = "user=admin&password=x7Kpk2mL9qRzVn"; # Noncompliant
user_password = "x7Kpk2mL9qRzVn"; # Noncompliant

user_password = GetPassword();
password = "login=a&password=#{user_password}"; # Compliant
password = "login=a&password=#$global_password"; # Compliant
password = "login=a&password=#@instance_field"; # Compliant
password = "login=a&password=#@@class_field"; # Compliant
