package com.team9.anicare.domain.information.service;

import com.team9.anicare.common.dto.PageDTO;
import com.team9.anicare.common.dto.PageMetaDTO;
import com.team9.anicare.common.dto.PageRequestDTO;
import com.team9.anicare.common.exception.CustomException;
import com.team9.anicare.common.exception.ResultCode;
import com.team9.anicare.common.file.service.S3FileService;
import com.team9.anicare.domain.information.dto.InformationRequestDTO;
import com.team9.anicare.domain.information.dto.InformationResponseDTO;
import com.team9.anicare.domain.information.model.Information;
import com.team9.anicare.domain.information.repository.InformationRepository;
import com.team9.anicare.domain.animal.model.Breed;
import com.team9.anicare.domain.animal.repository.BreedRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InformationService {

    private final InformationRepository informationRepository;
    private final BreedRepository breedRepository;
    private final ModelMapper modelMapper;
    private final S3FileService s3FileService;

    public void saveInformation(InformationRequestDTO informationDTO, MultipartFile file) {

        Breed breed = breedRepository.findByName(informationDTO.getBreedName());

        Information check = informationRepository.findBySpeciesAndBreed(informationDTO.getSpeciesName(), informationDTO.getBreedName());
        if(check != null){
            throw new CustomException(ResultCode.DUPLICATE_INFORMATION);
        }

        Information information = modelMapper.map(informationDTO, Information.class);
        information.setBreed(breed);

        try {
            if (file != null && !file.isEmpty()) {
                information.setPicture(s3FileService.uploadFile(file, "information"));
            }
        } catch (IOException e) {
            throw new CustomException(ResultCode.FILE_UPLOAD_ERROR);
        }

        informationRepository.save(information);
    }

    public PageDTO<InformationResponseDTO> getInformation(PageRequestDTO pageRequestDTO, String speciesName, String breedName) {
        PageRequest pageRequest = pageRequestDTO.toPageRequest();

        Page<Information> informationPage;
        if((speciesName == null || speciesName.isEmpty()) && (breedName == null || breedName.isEmpty())) {
            informationPage = informationRepository.findAllInformation(pageRequest);
        } else if(speciesName == null || speciesName.isEmpty()) {
            throw new CustomException(ResultCode.NOT_EXISTS_SPECIES);
        } else if(breedName == null || breedName.isEmpty()) {
            informationPage = informationRepository.findBySpeciesName(speciesName, pageRequest);
        } else {
            informationPage = informationRepository.findBySpeciesAndBreed(speciesName, breedName, pageRequest);
        }

        List<InformationResponseDTO> posts = informationPage.getContent().stream()
                .map(information -> {
                    InformationResponseDTO dto = modelMapper.map(information, InformationResponseDTO.class);
                    dto.setSpeciesName(information.getBreed().getSpecies().getName());
                    return dto;
                }).toList();
        System.out.println("posts:" + posts);

        PageMetaDTO meta = new PageMetaDTO(pageRequestDTO.getPage(), pageRequestDTO.getSize(), informationPage.getTotalElements());

        return new PageDTO<>(posts, meta);
    }

    public InformationResponseDTO getInformationDetail(Long informationId) {

        Information information = informationRepository.findById(informationId)
                .orElseThrow(() -> new CustomException(ResultCode.NOT_EXISTS_INFORMATION));

        information.setHit(information.getHit() + 1);
        informationRepository.save(information);

        InformationResponseDTO informationDTO = modelMapper.map(information, InformationResponseDTO.class);
        informationDTO.setSpeciesName(information.getBreed().getSpecies().getName());

        return informationDTO;
    }


}

