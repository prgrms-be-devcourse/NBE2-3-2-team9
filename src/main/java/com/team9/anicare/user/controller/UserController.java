package com.team9.anicare.user.controller;

import com.team9.anicare.common.Result;
import com.team9.anicare.user.dto.CreateAdminDTO;
import com.team9.anicare.user.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/admin")
    public Result signup(@Valid @RequestBody CreateAdminDTO createAdminDTO) {
        return userService.createAdmin(createAdminDTO);
    }
}
