/*
 * Copyright (c) 2018 Hai Zhang <dreaming.in.code.zh@gmail.com>
 * All Rights Reserved.
 */

package me.zhanghai.android.files.provider.archive;

import android.os.Parcel;
import android.os.Parcelable;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.threeten.bp.Instant;

import java.util.EnumSet;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java8.nio.file.Path;
import java8.nio.file.attribute.FileTime;
import me.zhanghai.android.files.provider.common.PosixFileAttributes;
import me.zhanghai.android.files.provider.common.PosixFileModeBit;
import me.zhanghai.android.files.provider.common.PosixFileType;
import me.zhanghai.android.files.provider.common.PosixGroup;
import me.zhanghai.android.files.provider.common.PosixUser;

public class ArchiveFileAttributes implements Parcelable, PosixFileAttributes {

    @NonNull
    private final String mEntryName;
    @NonNull
    private final FileTime mLastModifiedTime;
    @NonNull
    private final FileTime mLastAccessTime;
    @NonNull
    private final FileTime mCreationTime;
    @NonNull
    private final PosixFileType mType;
    private final long mSize;
    @NonNull
    private final ArchiveFileKey mFileKey;
    @Nullable
    private final PosixUser mOwner;
    @Nullable
    private final PosixGroup mGroup;
    @Nullable
    private final EnumSet<PosixFileModeBit> mMode;

    ArchiveFileAttributes(@NonNull Path archiveFile, @NonNull ArchiveEntry entry) {
        ArchiveFileAttributesImpl attributes = new ArchiveFileAttributesImpl(archiveFile, entry);
        mEntryName = attributes.getEntryName();
        mLastModifiedTime = attributes.lastModifiedTime();
        mLastAccessTime = attributes.lastAccessTime();
        mCreationTime = attributes.creationTime();
        mType = attributes.type();
        mSize = attributes.size();
        mFileKey = attributes.fileKey();
        mOwner = attributes.owner();
        mGroup = attributes.group();
        mMode = attributes.mode();
    }

    @NonNull
    public String getEntryName() {
        return mEntryName;
    }

    @Override
    @NonNull
    public FileTime lastModifiedTime() {
        return mLastModifiedTime;
    }

    @Override
    @NonNull
    public FileTime lastAccessTime() {
        return mLastAccessTime;
    }

    @Override
    @NonNull
    public FileTime creationTime() {
        return mCreationTime;
    }

    @NonNull
    public PosixFileType type() {
        return mType;
    }

    @Override
    public long size() {
        return mSize;
    }

    @Override
    @NonNull
    public Object fileKey() {
        return mFileKey;
    }

    @Override
    @Nullable
    public PosixUser owner() {
        return mOwner;
    }

    @Override
    @Nullable
    public PosixGroup group() {
        return mGroup;
    }

    @Nullable
    public Set<PosixFileModeBit> mode() {
        return mMode;
    }


    public static final Creator<ArchiveFileAttributes> CREATOR =
            new Creator<ArchiveFileAttributes>() {
                @Override
                public ArchiveFileAttributes createFromParcel(Parcel source) {
                    return new ArchiveFileAttributes(source);
                }

                @Override
                public ArchiveFileAttributes[] newArray(int size) {
                    return new ArchiveFileAttributes[size];
                }
            };

    protected ArchiveFileAttributes(Parcel in) {
        mEntryName = in.readString();
        mLastModifiedTime = FileTime.from((Instant) in.readSerializable());
        mLastAccessTime = FileTime.from((Instant) in.readSerializable());
        mCreationTime = FileTime.from((Instant) in.readSerializable());
        int tmpMType = in.readInt();
        mType = tmpMType == -1 ? null : PosixFileType.values()[tmpMType];
        mSize = in.readLong();
        mFileKey = in.readParcelable(ArchiveFileKey.class.getClassLoader());
        mOwner = in.readParcelable(PosixUser.class.getClassLoader());
        mGroup = in.readParcelable(PosixGroup.class.getClassLoader());
        //noinspection unchecked
        mMode = (EnumSet<PosixFileModeBit>) in.readSerializable();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mEntryName);
        dest.writeSerializable(mLastModifiedTime.toInstant());
        dest.writeSerializable(mLastAccessTime.toInstant());
        dest.writeSerializable(mCreationTime.toInstant());
        dest.writeInt(mType == null ? -1 : mType.ordinal());
        dest.writeLong(mSize);
        dest.writeParcelable(mFileKey, flags);
        dest.writeParcelable(mOwner, flags);
        dest.writeParcelable(mGroup, flags);
        dest.writeSerializable(mMode);
    }
}