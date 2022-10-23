package az.desofme.bank.beans;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Bean {

    @org.springframework.context.annotation.Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
