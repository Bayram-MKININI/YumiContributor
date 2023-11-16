package net.noliaware.yumi_contributor.commun.util

class PaginationException(val serviceError: ServiceError) : Exception(serviceError.toString())