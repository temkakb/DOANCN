package com.example.doancn.Models

data class Announcement(var Name: String, var content: String) {

    companion object {
        fun listOfAnnouncement(): ArrayList<Announcement> {
            val list = ArrayList<Announcement>()
            list.add(
                Announcement(
                    "Huấn Hoa Hồng",
                    "Ở cái xã hội này phải chịu khó làm, chịu khó học hỏi, khắc có tiền. Nay không kiếm được nhiều thì kiếm được ít, mình tích tiểu thành đại, mình chưa có thì mình không được chơi bời. Mình chưa có thì mình đừng có ăn chơi lêu lổng, đừng có a dua a tòng, đàn đúm."
                )
            )
            list.add(
                Announcement(
                    "Huấn Hoa Hồng",
                    "Chưa có thì mình phải làm, làm được bao nhiêu thì mình tiết kiệm, mình tích tiểu thành đại. Là một thằng đàn ông, kể cả mình bỏ ra 3 năm để làm ăn, kiếm tiền làm vốn chỉ làm ăn thôi, không giao lưu với đéo ai hết. Bởi vì khi các bạn đã không có tiền ý, đi giao lưu thì các bạn chỉ có thiệt thôi"
                )
            )
            return list
        }
    }

}