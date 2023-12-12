package com.xworkz.dream.util;

import com.xworkz.dream.dto.ClientDto;
import com.xworkz.dream.dto.ClientHrDto;
import com.xworkz.dream.dto.HrFollowUpDto;

public interface ClientInformationUtil {

	abstract void setValuesToClientHrDto(ClientHrDto dto);

	abstract void settingNaValues(HrFollowUpDto dto);

	public void setValuesToClientDto(ClientDto dto);

}
