package net.noliaware.yumi_contributor.commun.util

class PaginationException(val errorType: ErrorType) : Exception(errorType.toString())
