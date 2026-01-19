package ru.sellerbot.telegram.message;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.sellerbot.service.TelegramFileService;
import ru.sellerbot.service.TranscribeVoiceToTextService;


@Service
@RequiredArgsConstructor
public class TelegramVoiceHandler {

    private final TelegramFileService telegramFileService;
    private final TranscribeVoiceToTextService transcribeVoiceToTextService;
    private final ApplicationContext context;

    public TelegramTextHandler getTelegramTextHandler() {
        return context.getBean(TelegramTextHandler.class);
    }

    public SendMessage processVoice(Message message) {
        var chatId = message.getChatId();

        var voice = message.getVoice();
        var audio = message.getAudio();

        if (voice == null && audio == null) {
            return new SendMessage(chatId.toString(), "Не удалось распознать голосовое сообщение 🙁");
        }

        String fileId = voice != null ? voice.getFileId() : audio.getFileId();
        var file = telegramFileService.getFile(fileId);
        var text = transcribeVoiceToTextService.transcribe(file);

        message.setText(text);
        return getTelegramTextHandler().processTextMessage(message);
    }
}
