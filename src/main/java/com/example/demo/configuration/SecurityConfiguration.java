package com.example.demo.configuration;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import com.example.demo.controller.UserAuthService;

@EnableWebSecurity//Spring security default olarak http.formLogin aktif olarak gelir.(Dependency olarak ekledigimiz an login form cikartmaya baslar)Configuration classini ekleyip configure methodunu override ettikten sonra fomrLogin kullanilmaz
@EnableGlobalMethodSecurity(prePostEnabled = true) //Methodlardan once eklenecek anotation ile spel kullanimina izin veririz
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
	@Autowired
	UserAuthService userAuthService;

	@Override // Yetkilendirme ve temek konfigurasyon islemleri
	protected void configure(HttpSecurity http) throws Exception {
		http.csrf().disable(); //serverside rendering te formlardan gelen tokenlar icin, default active gelir ve disable ederiz
		http.headers().frameOptions().disable();
		http
			.httpBasic() //HttpBasic kullanacagimizi belirtiriz
			.authenticationEntryPoint(new AuthenticationEntryPoint() { //www-Authenticate : Basic realm="Realm" headerini onlemek icin
				//Annonymous Inner Type ile AuthenticationEntryPoint classi olusturduk ve default commence methodunda eklenen header disindakileri aynen ekledik.
				//Bunu yapmamizin nedeni; browser ile secured endpointlere ulasmaya calistigimizda , basic authentication gonderemedigimiz icin browser default olarak credentials soran bir pop up cikartir. O pop up'in olusmasini engellemek icin response.addHeader("WWW-Authenticate", "Basic realm=\"" + this.realmName + "\""); kismini  commence fonksiyonundan cikartiriz
				@Override
				public void commence(HttpServletRequest request, HttpServletResponse response,
						AuthenticationException authException) throws IOException, ServletException {
					response.sendError(HttpStatus.UNAUTHORIZED.value(), HttpStatus.UNAUTHORIZED.getReasonPhrase());
					
				}
			}); 
		http
			.authorizeRequests()
				.antMatchers("/global").authenticated() // Bu path authentication kontrol eder fakat authorization kontrol etmez
				.antMatchers(HttpMethod.PUT,"/api/1.0/users/{id}").authenticated()
			//	.antMatchers("/admin-page").hasAnyRole("admin") //ikinci bir yol olarak sadece bu satiri ekleyerekte authentication saglayabiliriz, @EnableGlobalMethodSecurity(prePostEnabled = true) ve method baslarina @PreAuthorize eklemek yerine
			.and()
			.authorizeRequests().anyRequest().permitAll();
		http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS); // Tum istekler icin credantials gonderilmesini zorunlu hale getiririz. Session uretimini stateless yaparak
		
	}
	
	@Override //User kontrol bcrypt gibi islemler
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		//Spring securitye username ve password kontrol edebilmesi icin bir servis veriyoruz ve bu servis icinde SecrutiyContextine principal gonderiyoruz
		auth.userDetailsService(userAuthService).passwordEncoder( passwordEncoder());
		
	}
	
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
