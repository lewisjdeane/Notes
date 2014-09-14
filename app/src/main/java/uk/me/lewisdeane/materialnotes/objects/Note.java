package uk.me.lewisdeane.materialnotes.objects;

/**
 * Created by Lewis on 05/08/2014.
 */
public final class Note extends Folder {

    private final String mTime;
    private final String mDate;
    private final String mLink;

    Note(Note.Builder _builder) {
        super(_builder);
        this.mTime = _builder.mTime;
        this.mDate = _builder.mDate;
        this.mLink = _builder.mLink;
    }

    public final String getTime() {
        return this.mTime;
    }

    public final String getDate() {
        return this.mDate;
    }

    public final String getLink() {
        return this.mLink;
    }

    @Override
    public NoteType getNoteType() {
        return NoteType.NOTE;
    }


    public void editToDatabase(Note _note) {

    }

    public static class Builder extends Folder.Builder {

        // Required params.
        private String mTime = "", mDate = "", mLink = "";

        // Basic constructor for required params.
        public Builder(String _path, String _title, String _item) {
            super(_path, _title, _item);
        }

        public final Builder time(String _time) {
            this.mTime = _time;
            return this;
        }

        public final Builder date(String _date) {
            this.mDate = _date;
            return this;
        }

        public final Builder link(String _link) {
            this.mLink = _link;
            return this;
        }

        public final Note build() {
            return new Note(this);
        }
    }
}