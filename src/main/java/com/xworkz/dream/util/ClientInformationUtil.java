package com.xworkz.dream.util;

import com.xworkz.dream.dto.ClientDto;
import com.xworkz.dream.dto.ClientHrDto;
import com.xworkz.dream.dto.HrFollowUpDto;

public interface ClientInformationUtil {

	void setValuesToClientHrDto(ClientHrDto dto);

	void settingNaValues(HrFollowUpDto dto);

	void setValuesToClientDto(ClientDto dto);

}
