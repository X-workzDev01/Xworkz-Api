package com.xworkz.dream.wrapper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.xworkz.dream.constants.FeesConstant;
import com.xworkz.dream.constants.SheetConstant;
import com.xworkz.dream.dto.AuditDto;
import com.xworkz.dream.dto.BatchDetailsDto;
import com.xworkz.dream.dto.utils.FeesUtils;
import com.xworkz.dream.feesDtos.EmailList;
import com.xworkz.dream.feesDtos.FeesDto;
import com.xworkz.dream.feesDtos.FeesHistoryDto;

@Service
public class FeesDetilesWrapperImpl implements FeesDetilesWrapper {
	@Autowired
	private FeesUtils feesUtiles;

	public static boolean validateCell(SheetConstant sheetConstant) {
		return StringUtils.hasLength(String.valueOf(sheetConstant.getIndex()));
	}

	@Override
	public FeesDto listToFeesDTO(List<Object> row) {
		if (row.size() > 1) {
			FeesDto feesDto = new FeesDto(0, null, new FeesHistoryDto(), null, null, null, 0, null, null, null, null,
					new AuditDto(), null, null);

			if (validateCell(SheetConstant.COLUMN_SL_NO)) {
				feesDto.getFeesHistoryDto()
						.setId(Integer.valueOf(row.get(SheetConstant.COLUMN_SL_NO.getIndex()).toString()));
			}
			if (validateCell(SheetConstant.COLUMN_SL_NO)) {
				feesDto.setId(Integer.valueOf(row.get(SheetConstant.COLUMN_SL_NO.getIndex()).toString()));
			}

			if (validateCell(SheetConstant.COLUMN__NAME)) {
				feesDto.setName((String) row.get(SheetConstant.COLUMN__NAME.getIndex()));
			}
			if (validateCell(SheetConstant.COLUMN_EMAIL)) {
				feesDto.getFeesHistoryDto().setEmail((String) row.get(SheetConstant.COLUMN_EMAIL.getIndex()));
			}

			if (validateCell(SheetConstant.COLUMN_LAST_FEES_PAID_DATE)) {
				feesDto.getFeesHistoryDto()
						.setLastFeesPaidDate((String) row.get(SheetConstant.COLUMN_LAST_FEES_PAID_DATE.getIndex()));
			}
			if (validateCell(SheetConstant.COLUMN_TRANSECTION_ID)) {
				feesDto.getFeesHistoryDto()
						.setTransectionId((String) row.get(SheetConstant.COLUMN_TRANSECTION_ID.getIndex()));
			}
			if (validateCell(SheetConstant.COLUMN_PAID_AMOUNT)) {
				feesDto.getFeesHistoryDto()
						.setPaidAmount((String) row.get(SheetConstant.COLUMN_PAID_AMOUNT.getIndex()));
			}
			if (validateCell(SheetConstant.COLUMN_PAYMENT_MODE)) {
				feesDto.getFeesHistoryDto()
						.setPaymentMode((String) row.get(SheetConstant.COLUMN_PAYMENT_MODE.getIndex()));
			}
			if (validateCell(SheetConstant.COLUMN__FOLLOWUP_STATUS)) {
				feesDto.getFeesHistoryDto()
						.setPaidTo((String) row.get(SheetConstant.COLUMN__FOLLOWUP_STATUS.getIndex()));
			}
			if (validateCell(SheetConstant.COLUMN_FEES_FOLLOWUP_DATE)) {
				feesDto.getFeesHistoryDto()
						.setFeesfollowupDate((String) row.get(SheetConstant.COLUMN_FEES_FOLLOWUP_DATE.getIndex()));
			}
			if (validateCell(SheetConstant.COLUMN_FOLLOWIUP_CALBACK_DATE)) {
				feesDto.getFeesHistoryDto().setFollowupCallbackDate(
						(String) row.get(SheetConstant.COLUMN_FOLLOWIUP_CALBACK_DATE.getIndex()));
			}
			if (validateCell(SheetConstant.COLUMN_REMINDER_DATE)) {
				feesDto.setReminderDate((String) row.get(SheetConstant.COLUMN_REMINDER_DATE.getIndex()));
			}
			if (validateCell(SheetConstant.COLUMN_FEES_CONCESSION)) {
				feesDto.setFeeConcession(
						Integer.valueOf(row.get(SheetConstant.COLUMN_FEES_CONCESSION.getIndex()).toString()));
			}

			if (validateCell(SheetConstant.COLUMN_MAIL_SEND_STATUS)) {
				feesDto.setMailSendStatus((String) row.get(SheetConstant.COLUMN_MAIL_SEND_STATUS.getIndex()));
			}
			if (validateCell(SheetConstant.COLUMN_COMMENTS)) {
				feesDto.setComments((String) row.get(SheetConstant.COLUMN_COMMENTS.getIndex()));
			}
			if (validateCell(SheetConstant.COLUMN_ADMIN_CREATED_BY)) {
				feesDto.getAdmin().setCreatedBy((String) row.get(SheetConstant.COLUMN_ADMIN_CREATED_BY.getIndex()));
			}
			if (validateCell(SheetConstant.COLUMN_ADMIN_CREATED_AT)) {
				feesDto.getAdmin().setCreatedOn((String) row.get(SheetConstant.COLUMN_ADMIN_CREATED_AT.getIndex()));
			}
			if (validateCell(SheetConstant.COLUMN_ADMIN_UPDATED_BY)) {
				feesDto.getAdmin().setUpdatedBy((String) row.get(SheetConstant.COLUMN_ADMIN_UPDATED_BY.getIndex()));
			}
			if (validateCell(SheetConstant.COLUMN_ADMIN_UPDATED_AT)) {
				feesDto.getAdmin().setUpdatedOn((String) row.get(SheetConstant.COLUMN_ADMIN_UPDATED_AT.getIndex()));
			}
			if (validateCell(SheetConstant.COLUMN_DATA_FLAG)) {
				feesDto.setSoftFlag((String) row.get(SheetConstant.COLUMN_DATA_FLAG.getIndex()));
			}
			if (validateCell(SheetConstant.LATE_FEES)) {
				feesDto.setLateFees(Long.valueOf(row.get(SheetConstant.LATE_FEES.getIndex()).toString()));
			}
			if (validateCell(SheetConstant.COLUMN_EMAIL)) {
				BatchDetailsDto details = feesUtiles
						.getBatchDetiles(row.get(SheetConstant.COLUMN_EMAIL.getIndex()).toString());
				if (details != null && details.getTotalAmount() != null) {
					feesDto.setCourseName((String) details.getCourseName());

					feesDto.setTotalAmount(details.getTotalAmount()
							- Long.valueOf(row.get(SheetConstant.COLUMN_FEES_CONCESSION.getIndex()).toString())
									* details.getTotalAmount() / 100
							+ Long.parseLong(row.get(SheetConstant.LATE_FEES.getIndex()).toString()));
					feesDto.setBalance(feesDto.getTotalAmount()
							- Long.valueOf((String) row.get(SheetConstant.COLUMN_PAID_AMOUNT.getIndex())));
					if (feesDto.getBalance() == 0L) {
						feesDto.setFeesStatus(FeesConstant.COMPLETED.toString());
					} else {
						feesDto.setFeesStatus((String) row.get(SheetConstant.COLUMN_FEES_STATUS.getIndex()));
					}
				}
			}
			return feesDto;
		}
		return new FeesDto();
	}

	public FeesHistoryDto getListToFeesHistoryDto(List<Object> row) {
		FeesHistoryDto feesHistoryDto = new FeesHistoryDto(null, null, null, null, null, null, null, null, null);

		if (validateCell(SheetConstant.COLUMN_SL_NO)) {
			feesHistoryDto.setId(Integer.valueOf(row.get(SheetConstant.COLUMN_SL_NO.getIndex()).toString()));
		}

		if (validateCell(SheetConstant.COLUMN_EMAIL)) {
			feesHistoryDto.setEmail((String) row.get(SheetConstant.COLUMN_EMAIL.getIndex() - 1));
		}
		if (validateCell(SheetConstant.COLUMN_TRANSECTION_ID)) {
			feesHistoryDto.setTransectionId((String) row.get(SheetConstant.COLUMN_TRANSECTION_ID.getIndex() - 1));
		}
		if (validateCell(SheetConstant.COLUMN_LAST_FEES_PAID_DATE)) {
			feesHistoryDto
					.setLastFeesPaidDate((String) row.get(SheetConstant.COLUMN_LAST_FEES_PAID_DATE.getIndex() - 1));
		}
		if (validateCell(SheetConstant.COLUMN_PAID_AMOUNT)) {
			feesHistoryDto.setPaidAmount((String) row.get(SheetConstant.COLUMN_PAID_AMOUNT.getIndex() - 1));
		}
		if (validateCell(SheetConstant.COLUMN_FEES_FOLLOWUP_DATE)) {
			feesHistoryDto
					.setFeesfollowupDate((String) row.get(SheetConstant.COLUMN_FEES_FOLLOWUP_DATE.getIndex() - 1));
		}
		if (validateCell(SheetConstant.COLUMN__FOLLOWUP_STATUS)) {
			feesHistoryDto.setPaidTo((String) row.get(SheetConstant.COLUMN__FOLLOWUP_STATUS.getIndex() - 1));
		}
		if (validateCell(SheetConstant.COLUMN_PAYMENT_MODE)) {
			feesHistoryDto.setPaymentMode((String) row.get(SheetConstant.COLUMN_PAYMENT_MODE.getIndex() - 1));
		}
		if (validateCell(SheetConstant.COLUMN_FOLLOWIUP_CALBACK_DATE)) {
			feesHistoryDto.setFollowupCallbackDate(
					(String) row.get(SheetConstant.COLUMN_FOLLOWIUP_CALBACK_DATE.getIndex() - 1));

		}

		return feesHistoryDto;

	}

	public EmailList listToEmail(List<Object> list) {
		EmailList emailList = new EmailList(null);
		if (validateCell(SheetConstant.COLUMN_EMAIL)) {
			emailList.setEmail((String) list.get(0));
		}
		return emailList;
	}

}
